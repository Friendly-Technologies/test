package com.friendly.services.settings.acs;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.settings.acs.AcsLicense;
import com.friendly.commons.models.settings.acs.AcsLicenses;
import com.friendly.commons.models.settings.acs.AddAcsLicense;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.base.XmlConverter;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.settings.acs.orm.acs.model.AcsLicenseParameterEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.settings.acs.orm.acs.repository.CpeSerialRepository;
import com.friendly.services.settings.acs.orm.acs.repository.LicenceRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.acs.mapper.AcsMapper;
import com.friendly.services.settings.acs.model.License;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.LicenseUtils;
import com.ftacs.Exception_Exception;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.LICENSE_NOT_UNIQUE;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcsLicenseService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final AcsMapper acsMapper;

    @NonNull
    private final LicenceRepository licenceRepository;

    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final CpeSerialRepository cpeSerialRepository;

    @NonNull
    private final UserService userService;

    @NonNull
    private final XmlConverter<License> licenseXmlConverter;


    public void checkLicense(final String token) {
        final Session session = jwtService.getSession(token);

        AcsProvider.getAcsWebService(session.getClientType()).checkLicense();
    }

    @Transactional
    public void addLicense(final String token, final AddAcsLicense rawLicense) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        final String xmlLicense = LicenseUtils.decryptLicense(rawLicense.getLicense());
        final License license = licenseXmlConverter.convertToObject(xmlLicense);
        validateLicense(license);

        final License totalLicense = getTotalLicense(license, clientType);
        final String totalLicenseXml = licenseXmlConverter.convertToXml(totalLicense, License.class);
        try {
            AcsProvider.getAcsWebService(clientType)
                    .addLicense(LicenseUtils.encryptLicense(totalLicenseXml));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }

        String zoneId = session.getZoneId();
        final String startTime = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(ZoneId.from(ZoneOffset.UTC))
                .format(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId));
        final License licenseWithStartTime = license.toBuilder()
                .startTime(startTime)
                .build();
        final String licenseXml = licenseXmlConverter.convertToXml(licenseWithStartTime, License.class);
        licenceRepository.addAcsLicense(LicenseUtils.encryptLicense(licenseXml),
                DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId));
    }

    public AcsLicense getLicense(final ClientType clientType, final String dateFormat) {
        List<AcsLicenseParameterEntity> licenseParameterEntityList = licenceRepository.findAll();
        License license = acsMapper.entitiesToLicense(licenseParameterEntityList);
        return licenseToAcsLicense(license,null, clientType, null, dateFormat, null, true);
    }

    public AcsLicenses getLicenses(final String token) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final String dateFormat = user.getDateFormat();

        final AcsLicense license = getLicense(clientType, dateFormat);
        final Long useDevices = license.getUseDevices();
        if (useDevices != null) {
            license.setLimitDevices(useDevices + "/" + license.getLimitDevices());
        }
        final Long useTR069 = license.getUseTR069();
        if (useTR069 != null) {
            license.setLimitTR069(useTR069 + "/" + license.getLimitTR069());
        }
        final Long useLWM2M = license.getUseLWM2M();
        if (useLWM2M != null) {
            license.setLimitLWM2M(useLWM2M + "/" + license.getLimitLWM2M());
        }
        final Long useMQTT = license.getUseMQTT();
        if (useMQTT != null) {
            license.setLimitMQTT(useMQTT + "/" + license.getLimitMQTT());
        }
        final Long useUSP = license.getUseUSP();
        if (useUSP != null) {
            license.setLimitUSP(useUSP + "/" + license.getLimitUSP());
        }

        return AcsLicenses.builder()
                .items(getLicenses(clientType, session.getZoneId(), dateFormat, user.getTimeFormat()))
                .total(license)
                .build();
    }

    private License getTotalLicense(final License license, final ClientType clientType) {
        final License currentLicense = acsMapper.entitiesToLicense(licenceRepository.findAll());

        final License.LicenseBuilder licenseBuilder = license.toBuilder();

        if (license.getCpeAdminUsers().equalsIgnoreCase("unlimited")) {
            licenseBuilder.cpeAdminUsers("0");
        } else if (license.getCpeAdminUsers().equalsIgnoreCase("locked")) {
            licenseBuilder.cpeAdminUsers("-1");
        }

        if (license.getCsrUsers().equalsIgnoreCase("unlimited")) {
            licenseBuilder.csrUsers("0");
        } else if (license.getCsrUsers().equalsIgnoreCase("locked")) {
            licenseBuilder.csrUsers("-1");
        }

        if (license.getManagedCpe().equalsIgnoreCase("unlimited")) {
            licenseBuilder.managedCpe("0");
        } else if (license.getManagedCpe().equalsIgnoreCase("locked")) {
            licenseBuilder.managedCpe("-1");
        } else if (NumberUtils.isCreatable(license.getManagedCpe())
                && NumberUtils.isCreatable(currentLicense.getManagedCpe())) {
            licenseBuilder.managedCpe(String.valueOf(Integer.parseInt(license.getManagedCpe()) +
                    Integer.parseInt(currentLicense.getManagedCpe())));
        }

        if (license.getManagedCpeTR069().equalsIgnoreCase("unlimited")) {
            licenseBuilder.managedCpeTR069("0");
        } else if (license.getManagedCpeTR069().equalsIgnoreCase("locked")) {
            licenseBuilder.managedCpeTR069("-1");
        } else if (NumberUtils.isCreatable(license.getManagedCpeTR069())
                && NumberUtils.isCreatable(currentLicense.getManagedCpeTR069())) {
            licenseBuilder.managedCpeTR069(
                    String.valueOf(Integer.parseInt(license.getManagedCpeTR069()) +
                            Integer.parseInt(currentLicense.getManagedCpeTR069())));
        }

        if (license.getManagedCpeLWM2M().equalsIgnoreCase("unlimited")) {
            licenseBuilder.managedCpeLWM2M("0");
        } else if (license.getManagedCpeLWM2M().equalsIgnoreCase("locked")) {
            licenseBuilder.managedCpeLWM2M("-1");
        } else if (NumberUtils.isCreatable(license.getManagedCpeLWM2M())
                && NumberUtils.isCreatable(currentLicense.getManagedCpeLWM2M())) {
            licenseBuilder.managedCpeLWM2M(
                    String.valueOf(Integer.parseInt(license.getManagedCpeLWM2M()) +
                            Integer.parseInt(currentLicense.getManagedCpeLWM2M())));
        }

        if (license.getManagedCpeMQTT().equalsIgnoreCase("unlimited")) {
            licenseBuilder.managedCpeMQTT("0");
        } else if (license.getManagedCpeMQTT().equalsIgnoreCase("locked")) {
            licenseBuilder.managedCpeMQTT("-1");
        } else if (NumberUtils.isCreatable(license.getManagedCpeMQTT())
                && NumberUtils.isCreatable(currentLicense.getManagedCpeMQTT())) {
            licenseBuilder.managedCpeMQTT(
                    String.valueOf(Integer.parseInt(license.getManagedCpeMQTT()) +
                            Integer.parseInt(currentLicense.getManagedCpeMQTT())));
        }

        if (license.getManagedCpeUSP().equalsIgnoreCase("unlimited")) {
            licenseBuilder.managedCpeUSP("0");
        } else if (license.getManagedCpeUSP().equalsIgnoreCase("locked")) {
            licenseBuilder.managedCpeUSP("-1");
        } else if (NumberUtils.isCreatable(license.getManagedCpeUSP())
                && NumberUtils.isCreatable(currentLicense.getManagedCpeUSP())) {
            licenseBuilder.managedCpeUSP(
                    String.valueOf(Integer.parseInt(license.getManagedCpeUSP()) +
                            Integer.parseInt(currentLicense.getManagedCpeUSP())));
        }

        if (license.getRegisteredCpe().equalsIgnoreCase("unlimited")) {
            licenseBuilder.registeredCpe("0");
        } else if (license.getRegisteredCpe().equalsIgnoreCase("locked")) {
            licenseBuilder.registeredCpe("-1");
        } else if (NumberUtils.isCreatable(license.getRegisteredCpe())
                && NumberUtils.isCreatable(currentLicense.getRegisteredCpe())) {
            licenseBuilder.registeredCpe(
                    String.valueOf(Integer.parseInt(license.getRegisteredCpe()) +
                            Integer.parseInt(currentLicense.getRegisteredCpe())));
        }

        if (license.getRegisteredCpeTR069().equalsIgnoreCase("unlimited")) {
            licenseBuilder.registeredCpeTR069("0");
        } else if (license.getRegisteredCpeTR069().equalsIgnoreCase("locked")) {
            licenseBuilder.registeredCpeTR069("-1");
        } else if (NumberUtils.isCreatable(license.getRegisteredCpeTR069())
                && NumberUtils.isCreatable(currentLicense.getRegisteredCpeTR069())) {
            licenseBuilder.registeredCpeTR069(
                    String.valueOf(Integer.parseInt(license.getRegisteredCpeTR069()) +
                            Integer.parseInt(currentLicense.getRegisteredCpeTR069())));
        }

        if (license.getRegisteredCpeLWM2M().equalsIgnoreCase("unlimited")) {
            licenseBuilder.registeredCpeLWM2M("0");
        } else if (license.getRegisteredCpeLWM2M().equalsIgnoreCase("locked")) {
            licenseBuilder.registeredCpeLWM2M("-1");
        } else if (NumberUtils.isCreatable(license.getRegisteredCpeLWM2M())
                && NumberUtils.isCreatable(currentLicense.getRegisteredCpeLWM2M())) {
            licenseBuilder.registeredCpeLWM2M(
                    String.valueOf(Integer.parseInt(license.getRegisteredCpeLWM2M()) +
                            Integer.parseInt(currentLicense.getRegisteredCpeLWM2M())));
        }

        if (license.getRegisteredCpeMQTT().equalsIgnoreCase("unlimited")) {
            licenseBuilder.registeredCpeMQTT("0");
        } else if (license.getRegisteredCpeMQTT().equalsIgnoreCase("locked")) {
            licenseBuilder.registeredCpeMQTT("-1");
        } else if (NumberUtils.isCreatable(license.getRegisteredCpeMQTT())
                && NumberUtils.isCreatable(currentLicense.getRegisteredCpeMQTT())) {
            licenseBuilder.registeredCpeMQTT(
                    String.valueOf(Integer.parseInt(license.getRegisteredCpeMQTT()) +
                            Integer.parseInt(currentLicense.getRegisteredCpeMQTT())));
        }

        if (license.getRegisteredCpeUSP().equalsIgnoreCase("unlimited")) {
            licenseBuilder.registeredCpeUSP("0");
        } else if (license.getRegisteredCpeUSP().equalsIgnoreCase("locked")) {
            licenseBuilder.registeredCpeUSP("-1");
        } else if (NumberUtils.isCreatable(license.getRegisteredCpeUSP())
                && NumberUtils.isCreatable(currentLicense.getRegisteredCpeUSP())) {
            licenseBuilder.registeredCpeUSP(
                    String.valueOf(Integer.parseInt(license.getRegisteredCpeUSP()) +
                            Integer.parseInt(currentLicense.getRegisteredCpeUSP())));
        }

        final int dayCount = NumberUtils.isCreatable(license.getDayCount())
                ? Integer.parseInt(license.getDayCount()) : 0;
        if (license.getTimeExpiration().equalsIgnoreCase("unlimited")
                || license.getTimeExpiration().equals("0") || dayCount == 0) {
            licenseBuilder.timeExpiration(null);
        } else if (dayCount > 0) {
            final String timeExpiration =
                    DateTimeUtils.formatAcsWithDate(Instant.now().plus(dayCount, ChronoUnit.DAYS), clientType,
                            ZoneId.from(ZoneOffset.UTC).getId(), "dd/MM/yyyy");
            licenseBuilder.timeExpiration(timeExpiration);
        }

        return licenseBuilder.build();
    }

    private void validateLicense(final License license) {
        final boolean isIdNotUnique = licenceRepository.getAcsLicenses()
                .stream()
                .map(l -> licenseXmlConverter.convertToObject(
                        LicenseUtils.decryptLicense((String) l.get("value"))))
                .map(License::getUniqueId)
                .anyMatch(id -> id.equals(license.getUniqueId()));
        if (isIdNotUnique) {
            throw new FriendlyIllegalArgumentException(LICENSE_NOT_UNIQUE);
        }
    }

    private List<AcsLicense> getLicenses(final ClientType clientType, final String zoneId,
                                         final String dateFormat, final String timeFormat) {
        return licenceRepository.getAcsLicenses()
                .stream()
                .map(license -> licenseToAcsLicense(
                        licenseXmlConverter.convertToObject(
                                LicenseUtils.decryptLicense((String) license.get("value"))),
                        ((Timestamp) license.get("created")).toInstant(), clientType, zoneId,
                        dateFormat, timeFormat, false))
                .collect(Collectors.toList());
    }

    private AcsLicense licenseToAcsLicense(final License license, final Instant created,
                                           final ClientType clientType, final String zoneId,
                                           final String dateFormat, final String timeFormat,
                                           final boolean isTotal) {
        final AcsLicense.AcsLicenseBuilder builder = AcsLicense.builder();
        builder.customerName(license.getCustomerName());

        if (created != null) {
            builder.createdIso(created);
            builder.created(DateTimeUtils.formatAcs(created, clientType, zoneId, dateFormat, timeFormat));
        }

        builder.type(findLicenceType(license));

        final String expireTime = license.getTimeExpiration();
        final String dayCount = license.getDayCount();
        if (expireTime != null && (expireTime.isEmpty() || isUnlimited(expireTime))) {
            builder.timeLimit("unlimited");
        } else if (expireTime != null) {
            final Instant expireDateIso = LocalDate.parse(expireTime,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .atStartOfDay(ZoneId.from(ZoneOffset.UTC))
                    .toInstant();
            builder.expireDateIso(expireDateIso);
            builder.timeLimit(DateTimeUtils.formatAcsWithDate(expireDateIso, clientType,
                    ZoneId.from(ZoneOffset.UTC).getId(), dateFormat));
        } else if (NumberUtils.isCreatable(dayCount)
                && (Integer.parseInt(dayCount) == 0 || Integer.parseInt(dayCount) == Integer.MAX_VALUE)) {
            builder.timeLimit("unlimited");
        } else if (dayCount != null) {
            if (isTotal && license.getStartTime() != null) {
                final Instant startDateIso = LocalDate.parse(license.getStartTime(),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        .atStartOfDay(ZoneId.from(ZoneOffset.UTC))
                        .toInstant();
                final Instant expireDateIso = startDateIso.plus(Integer.parseInt(dayCount), ChronoUnit.DAYS);
                builder.expireDateIso(expireDateIso);
                builder.timeLimit(DateTimeUtils.formatAcsWithDate(expireDateIso, clientType,
                        ZoneId.from(ZoneOffset.UTC).getId(), dateFormat));
            } else {
                builder.timeLimit(dayCount);
            }
        }

        final String cpeAdminUsers = license.getCpeAdminUsers();
        if (isUnlimited(cpeAdminUsers)) {
            builder.cpeAdminUsers("unlimited");
        } else if (cpeAdminUsers != null) {
            builder.cpeAdminUsers(cpeAdminUsers);
        }

        final String csrUsers = license.getCsrUsers();
        if (isUnlimited(csrUsers)) {
            builder.csrUsers("unlimited");
        } else if (csrUsers != null) {
            builder.csrUsers(csrUsers);
        }

        final String managedCpe = license.getManagedCpe();
        final String managedCpeTR069 = license.getManagedCpeTR069();
        final String managedCpeLWM2M = license.getManagedCpeLWM2M();
        final String managedCpeMQTT = license.getManagedCpeMQTT();
        final String managedCpeUSP = license.getManagedCpeUSP();

        if ((managedCpe != null && !isUnlimited(managedCpe))
                || (managedCpeTR069 != null && !isUnlimited(managedCpeTR069))
                || (managedCpeLWM2M != null && !isUnlimited(managedCpeLWM2M))
                || (managedCpeMQTT != null && !isUnlimited(managedCpeMQTT))
                || (managedCpeUSP != null && !isUnlimited(managedCpeUSP))) {
            setCpeLimits(builder, managedCpe, managedCpeTR069, managedCpeLWM2M, managedCpeMQTT, managedCpeUSP,
                    isTotal, true);
        } else {
            setCpeLimits(builder, license.getRegisteredCpe(), license.getRegisteredCpeTR069(),
                    license.getRegisteredCpeLWM2M(), license.getRegisteredCpeMQTT(),
                    license.getRegisteredCpeUSP(), isTotal, false);
        }

        return builder.build();
    }

    private String findLicenceType(License license) {
        boolean isManaged = false;
        boolean isRegistered = false;
        String type = Strings.EMPTY;

        Map<String, String> keys = new HashMap<>();
        keys.put(license.getManagedCpeUSP(), license.getRegisteredCpeUSP());
        keys.put(license.getManagedCpeLWM2M(), license.getRegisteredCpeLWM2M());
        keys.put(license.getManagedCpeTR069(), license.getRegisteredCpeTR069());
        keys.put(license.getManagedCpeMQTT(), license.getRegisteredCpeMQTT());
        keys.put(license.getManagedCpe(), license.getRegisteredCpe());

        for (Map.Entry<String, String> item : keys.entrySet()) {
            String managed = item.getKey();
            String registered = item.getValue();
            if ((managed != null && registered != null) && (isNotDisabled(managed) && isNotDisabled(registered))
                    && (!isUnlimited(registered) || !isUnlimited(managed))) {
                if (isUnlimited(managed)) {
                    isRegistered = true;
                } else {
                    isManaged = true;
                }
            }

            if (isRegistered) {
                type = "R";
            } else if (isManaged) {
                type = "M";
            } else {
                type = "U";
            }
        }
        return type;
    }

    private void setCpeLimits(final AcsLicense.AcsLicenseBuilder builder, final String limitCpe,
                              final String limitCpeTR069, final String limitCpeLWM2M,
                              final String limitCpeMQTT, final String limitCpeUSP,
                              final boolean isTotal, final boolean isManaged) {
        if (isUnlimited(limitCpe)) {
            builder.limitDevices("unlimited");
        } else if (limitCpe != null && limitCpe.equals("-1")) {
            builder.limitDevices("locked");
        } else if (limitCpe != null) {
            builder.limitDevices(limitCpe);
            if (isTotal && isManaged) {
                builder.useDevices(cpeRepository.count());
            } else if (isTotal) {
                builder.useDevices(cpeSerialRepository.count());
            }
        }

        if (isUnlimited(limitCpeTR069)) {
            builder.limitTR069("unlimited");
        } else if (limitCpeTR069 != null && limitCpeTR069.equals("-1")) {
            builder.limitTR069("locked");
        } else if (limitCpeTR069 != null) {
            builder.limitTR069(limitCpeTR069);
            if (isTotal && isManaged) {
                builder.useTR069(cpeRepository.countByProtocolId(
                        DeviceUtils.convertProtocolTypeToId(ProtocolType.TR069)));
            } else if (isTotal) {
                builder.useTR069(cpeSerialRepository.countByProtocolId(
                        DeviceUtils.convertProtocolTypeToId(ProtocolType.TR069)));
            }
        }

        if (isUnlimited(limitCpeLWM2M)) {
            builder.limitLWM2M("unlimited");
        } else if (limitCpeLWM2M != null && limitCpeLWM2M.equals("-1")) {
            builder.limitLWM2M("locked");
        } else if (limitCpeLWM2M != null) {
            builder.limitLWM2M(limitCpeLWM2M);
            if (isTotal && isManaged) {
                builder.useLWM2M(cpeRepository.countByProtocolId(
                        DeviceUtils.convertProtocolTypeToId(ProtocolType.LWM2M)));
            } else if (isTotal) {
                builder.useLWM2M(cpeSerialRepository.countByProtocolId(
                        DeviceUtils.convertProtocolTypeToId(ProtocolType.LWM2M)));
            }
        }

        if (isUnlimited(limitCpeMQTT)) {
            builder.limitMQTT("unlimited");
        } else if (limitCpeMQTT != null && limitCpeMQTT.equals("-1")) {
            builder.limitMQTT("locked");
        } else if (limitCpeMQTT != null) {
            builder.limitMQTT(limitCpeMQTT);
            if (isTotal && isManaged) {
                builder.useMQTT(cpeRepository.countByProtocolId(
                        DeviceUtils.convertProtocolTypeToId(ProtocolType.MQTT)));
            } else if (isTotal) {
                builder.useMQTT(cpeSerialRepository.countByProtocolId(
                        DeviceUtils.convertProtocolTypeToId(ProtocolType.MQTT)));
            }
        }

        if (isUnlimited(limitCpeUSP)) {
            builder.limitUSP("unlimited");
        } else if (limitCpeUSP != null && limitCpeUSP.equals("-1")) {
            builder.limitUSP("locked");
        } else if (limitCpeUSP != null) {
            builder.limitUSP(limitCpeUSP);
            if (isTotal && isManaged) {
                builder.useUSP(cpeRepository.countByProtocolId(
                        DeviceUtils.convertProtocolTypeToId(ProtocolType.USP)));
            } else if (isTotal) {
                builder.useUSP(cpeSerialRepository.countByProtocolId(
                        DeviceUtils.convertProtocolTypeToId(ProtocolType.USP)));
            }
        }
    }

    private boolean isUnlimited(String limit) {
        if (limit == null) {
            return false;
        }
        return limit.equals("0") || limit.equalsIgnoreCase("unlimited");
    }

    private boolean isNotDisabled(String limit) {
        return !isDisabled(limit);
    }

    private boolean isDisabled(String limit) {
        if (limit == null) {
            return false;
        }
        return limit.equals("-1");
    }

}
