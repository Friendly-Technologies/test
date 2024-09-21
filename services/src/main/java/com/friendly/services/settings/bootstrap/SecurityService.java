package com.friendly.services.settings.bootstrap;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.FTPageDetails;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.file.FileDownloadRequest;
import com.friendly.commons.models.settings.iot.DeleteIotSecurityBody;
import com.friendly.commons.models.settings.iot.Lwm2mSecurityBody;
import com.friendly.commons.models.settings.iot.MqttSecurityBody;
import com.friendly.commons.models.settings.iot.SecurityModesBody;
import com.friendly.commons.models.settings.iot.UspSecurityBody;
import com.friendly.commons.models.settings.iot.response.MaskTypesResponse;
import com.friendly.commons.models.settings.iot.response.SecurityModeTypesResponse;
import com.friendly.commons.models.settings.iot.response.ServerTypesResponse;
import com.friendly.commons.models.settings.response.SecurityUspMtpModeResponse;
import com.friendly.commons.models.settings.security.AbstractSecurity;
import com.friendly.commons.models.settings.security.MqttSecurityType;
import com.friendly.commons.models.settings.security.ProtocolSecurityType;
import com.friendly.commons.models.settings.security.SecurityDetailBody;
import com.friendly.commons.models.settings.security.SecurityLWM2M;
import com.friendly.commons.models.settings.security.SecurityLWM2MDetails;
import com.friendly.commons.models.settings.security.SecurityMQTT;
import com.friendly.commons.models.settings.security.SecurityMQTTDetails;
import com.friendly.commons.models.settings.security.SecurityUSP;
import com.friendly.commons.models.settings.security.SecurityUSPDetails;
import com.friendly.commons.models.settings.security.SecurityUSPDetailsRequest;
import com.friendly.commons.models.settings.security.SecurityUspMtp;
import com.friendly.commons.models.settings.security.SecurityUspMtpRequest;
import com.friendly.commons.models.settings.security.ServerType;
import com.friendly.commons.models.settings.security.UnderlyingProtocolType;
import com.friendly.commons.models.settings.security.add.AddSecurityLWM2M;
import com.friendly.commons.models.settings.security.add.AddSecurityMQTT;
import com.friendly.commons.models.settings.security.add.AddSecurityUSP;
import com.friendly.commons.models.settings.security.add.SecurityDetailUSP;
import com.friendly.commons.models.settings.security.auth.AuthBasic;
import com.friendly.commons.models.settings.security.auth.AuthPsk;
import com.friendly.commons.models.settings.security.auth.AuthPublicKey;
import com.friendly.commons.models.settings.security.auth.AuthX509;
import com.friendly.commons.models.settings.security.auth.AuthX509USP;
import com.friendly.commons.models.settings.security.auth.SecurityModeType;
import com.friendly.commons.models.settings.security.oscore.Oscore;
import com.friendly.commons.models.settings.security.oscore.SecurityUspMtpMode;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.filemanagement.service.DeviceFileService;
import com.friendly.services.filemanagement.orm.acs.model.FileTypeEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityMqttEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityUspEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityUspMtpEntity;
import com.friendly.services.filemanagement.orm.acs.repository.FileTypeRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.SecurityLwm2mRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.SecurityMqttRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.SecurityUspRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.UspMtpRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.bootstrap.mapper.IotMapper;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.ftacs.SecurityConfigurationDetailsWs;
import com.ftacs.SecurityConfigurationWs;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.commons.models.settings.security.ServerType.ALL;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.PSK;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.X509;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.X_509;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FILE_FORMAT_NOT_SUPPORTED;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PARAMETER_NOT_UNIQUE;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final UserService userService;

    @NonNull
    private final DomainService domainService;

    @NonNull
    private final SecurityLwm2mRepository lwm2mRepository;

    @NonNull
    private final SecurityMqttRepository mqttRepository;

    @NonNull
    private final SecurityUspRepository uspRepository;

    @NonNull
    private final UspMtpRepository uspMtpRepository;

    @NonNull
    private final FileTypeRepository fileTypeRepository;

    @NonNull
    private final DeviceFileService fileService;

    @NonNull
    private final IotMapper iotMapper;


    public FTPage<AbstractSecurity> getLWM2MSecurityPage(final String token,
                                                         final Lwm2mSecurityBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest
                (body.getPageNumbers(), body.getPageSize(), null, "id");
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final String zoneId = session.getZoneId();
        final Integer domainId = body.getDomainId();
        final String searchParam = body.getSearchParam();

        final UserResponse user = userService.getUser(session.getUserId(), zoneId);
        final List<Integer> domainIds = domainId == null
                ? domainService.getChildDomainIds(user.getDomainId())
                : domainService.getChildDomainIds(domainId);

        final FTPage<AbstractSecurity> securityPage = new FTPage<>();
        final List<AbstractSecurity> securities;
        final FTPageDetails pageDetails;
        final List<Page<SecurityLwm2mEntity>> lwm2mSecurityEntities =
                pageable.stream()
                        .map(p -> getLwm2mSecurity(searchParam, body.getSearchExact(),
                                domainIds, body.getServerType(), p))
                        .collect(Collectors.toList());
        securities = lwm2mSecurityEntities.stream()
                .map(Page::getContent)
                .flatMap(entities -> entities.stream()
                        .map(e -> entityToLwm2mSecurity(e,
                                clientType,
                                zoneId,
                                user.getDateFormat(),
                                user.getTimeFormat())))
                .collect(Collectors.toList());
        pageDetails = PageUtils.buildPageDetails(lwm2mSecurityEntities);
        return securityPage.toBuilder()
                .items(securities)
                .pageDetails(pageDetails)
                .build();

    }

    public ServerTypesResponse getIotSecurityServerTypes(final String token, final ProtocolSecurityType protocolType) {
        jwtService.getSession(token);

        return new ServerTypesResponse(protocolType.getServerTypes());
    }

    public SecurityModeTypesResponse getIotSecurityModes(final String token,
                                                         final SecurityModesBody body) {
        jwtService.getSession(token);
        final ProtocolSecurityType protocolType = body.getProtocolType();

        if (protocolType == ProtocolSecurityType.USP) {
            return new SecurityModeTypesResponse(body.getProtocolType().getSecurityModes());
        }
        return new SecurityModeTypesResponse(protocolType.getSecurityModes());
    }

    public MaskTypesResponse getIotSecurityMasks(final String token, final ProtocolSecurityType protocolType) {
        jwtService.getSession(token);

        return new MaskTypesResponse(protocolType.getMaskTypes());
    }

    @Transactional
    public void addSecurity(final String token, String file/*final AddAbstractSecurity security,*/
                           /* final MultipartFile certificate*/) {
         final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        if (file == null) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
        final FileDownloadRequest downloadRequest =
                FileDownloadRequest.builder()
                        .sendBytes(true)
                        .fileTypeId(fileTypeRepository.findAllByType("LWM2M PSK Credentials")
                                .stream()
                                .map(FileTypeEntity::getId)
                                .findAny()
                                .orElse(null))
                        .fileContent(file)
                        .build();
                fileService.fileDownload(null, downloadRequest, clientType,
                        userService.getUser(session.getUserId(), session.getZoneId()), null, session, false);
           }

    @Transactional
    public boolean deleteLWM2MSecurity(final String token,
                                       final DeleteIotSecurityBody body) {
        final Session session = jwtService.getSession(token);
        final List<Integer> ids = body.getIds();
        final List<SecurityLwm2mEntity> lwm2m = lwm2mRepository.findAllById(ids);
        if (!lwm2m.isEmpty()) {
            lwm2mRepository.deleteInBatch(lwm2m);
            return true;
        }
        return false;
    }

    private void saveLwm2mSecurity(final AddSecurityLWM2M security, final Long userId,
                                   final String zoneId, final ClientType clientType) {
        validateLwm2m(security);
        final Optional<SecurityLwm2mEntity> currentLwm2m = lwm2mRepository.findById(security.getId());
        final UserResponse user = userService.getUser(userId, zoneId);
        final SecurityModeType securityMode = security.getAuth().getSecurityMode();
        final String identity;
        final String privateKey;
        final String publicKey;

        switch (securityMode) {
            case PSK:
                final AuthPsk authPsk = (AuthPsk) security.getAuth();
                identity = authPsk.getIdentity();
                privateKey = authPsk.getPrivateKey();
                publicKey = null;
                break;
            case X_509:
                final AuthX509 authX509 = (AuthX509) security.getAuth();
                identity = authX509.getClientCertificate();
                privateKey = authX509.getPrivateKey();
                publicKey = authX509.getServerCertificate();
                break;
            default:
                identity = null;
                privateKey = null;
                publicKey = null;
                break;
        }

        final SecurityLwm2mEntity.SecurityLwm2mEntityBuilder<?, ?> lwm2mBuilder;
        if (currentLwm2m.isPresent()) {
            lwm2mBuilder = currentLwm2m.get()
                    .toBuilder()
                    .updated(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId))
                    .updater(clientType.name().toUpperCase() + "/" + user.getName());
        } else {
            lwm2mBuilder = SecurityLwm2mEntity.builder()
                    .domainId(security.getDomainId() != null ? security.getDomainId() :
                            domainService.getDomainIdByUserId(userId)
                                    .orElse(null))
                    .created(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId))
                    .creator(clientType.name().toUpperCase() + "/" + user.getName());
        }
        lwm2mBuilder.serverType(security.getServerType())
                .mask(security.getMask())
                .maskType(security.getMaskType())
                .securityMode(securityMode.getOrdinal())
                .identity(identity)
                .secretKey(privateKey)
                .publicKey(publicKey);
        final Oscore oscore = security.getOscore();
        if (oscore != null && StringUtils.isNotBlank(oscore.getMasterSecret())) {
            lwm2mBuilder.senderId(oscore.getSenderId())
                    .recipientId(oscore.getRecipientId())
                    .masterSecret(oscore.getMasterSecret())
                    .masterSalt(oscore.getMasterSalt())
                    .aeadAlgorithmType(oscore.getAeadAlgorithm())
                    .hmacAlgorithmType(oscore.getHmacAlgorithm());
        }
        lwm2mRepository.saveAndFlush(lwm2mBuilder.build());
    }

    private void validateLwm2m(final AddSecurityLWM2M lwm2M) {
        if (lwm2M.getOscore() != null && StringUtils.isNotBlank(lwm2M.getOscore().getSenderId())) {
            if (lwm2mRepository.existsByIdNotAndSenderId(lwm2M.getId(), lwm2M.getOscore().getSenderId())) {
                throw new FriendlyIllegalArgumentException(PARAMETER_NOT_UNIQUE, "Sender Id");
            }
        }
        if (lwm2M.getAuth() != null && lwm2M.getAuth().getSecurityMode() == PSK) {
            final AuthPsk auth = (AuthPsk) lwm2M.getAuth();
            if (lwm2mRepository.existsByIdNotAndIdentity(lwm2M.getId(), auth.getIdentity())) {
                throw new FriendlyIllegalArgumentException(PARAMETER_NOT_UNIQUE, "Identity");
            }
        } else if (lwm2M.getAuth() != null) {
            if (lwm2mRepository.existsByIdNotAndSecurityModeAndMaskLike(lwm2M.getId(),
                    lwm2M.getAuth().getSecurityMode().getOrdinal(),
                    lwm2M.getMask())) {
                throw new FriendlyIllegalArgumentException(PARAMETER_NOT_UNIQUE, "Mask");
            }
        }
    }

    private void saveMqttSecurity(final AddSecurityMQTT security, final Long userId,
                                  final String zoneId, final ClientType clientType) {
        validateMqtt(security);
        final Optional<SecurityMqttEntity> currentMqtt = mqttRepository.findById(security.getId());
        final UserResponse user = userService.getUser(userId, zoneId);
        final SecurityModeType securityMode = security.getAuth().getSecurityMode();
        final String login;
        final String password;

        if (securityMode == SecurityModeType.BASIC) {
            final AuthBasic authPsk = (AuthBasic) security.getAuth();
            login = authPsk.getLogin();
            password = authPsk.getPassword();
        } else {
            login = null;
            password = null;
        }

        final SecurityMqttEntity.SecurityMqttEntityBuilder<?, ?> mqttBuilder;
        if (currentMqtt.isPresent()) {
            mqttBuilder = currentMqtt.get()
                    .toBuilder()
                    .updated(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId))
                    .updater(clientType.name().toUpperCase() + "/" + user.getName());
        } else {
            mqttBuilder = SecurityMqttEntity.builder()
                    .domainId(security.getDomainId() != null ? security.getDomainId() :
                            domainService.getDomainIdByUserId(userId)
                                    .orElse(null))
                    .created(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId))
                    .creator(clientType.name().toUpperCase() + "/" + user.getName());
        }
        mqttBuilder.mask(security.getMask())
                .maskType(security.getMaskType())
                .securityType(securityMode.getOrdinal())
                .login(login)
                .password(password);
        mqttRepository.saveAndFlush(mqttBuilder.build());
    }

    private void validateMqtt(final AddSecurityMQTT mqtt) {
        if (mqttRepository.existsByIdNotAndSecurityModeAndMaskLike(mqtt.getId(),
                mqtt.getAuth().getSecurityMode().getOrdinal(),
                mqtt.getMask())) {
            throw new FriendlyIllegalArgumentException(PARAMETER_NOT_UNIQUE, "Mask");
        }
    }

    private void saveUspSecurity(final AddSecurityUSP security, final Long userId, final ClientType clientType,
                                 final String certificate) {
        validateUps(security);
        final SecurityConfigurationWs securityWs = new SecurityConfigurationWs();
        securityWs.setActive(security.getActive());
        securityWs.setId(security.getId());
        securityWs.setIdentifier(security.getMask());
        securityWs.setIdentifierType("ENDPOINT_MASK");
        securityWs.setDescription(security.getDescription());
        securityWs.setLocationId(security.getDomainId() != null ? security.getDomainId() :
                domainService.getDomainIdByUserId(userId)
                        .orElse(null));
        securityWs.setProtocolId(5);
        securityWs.setSecurityDestinationType("ALL");
        final List<SecurityConfigurationDetailsWs> detailsWs =
                security.getDetails()
                        .stream()
                        .map(s -> getDetailsWs(s, certificate))
                        .collect(Collectors.toList());
        securityWs.getSecurityConfigurationDetails().addAll(detailsWs);

        AcsProvider.getAcsWebService(clientType).saveOrUpdateSecurityConfiguration(securityWs);
    }

    private SecurityConfigurationDetailsWs getDetailsWs(final SecurityDetailUSP s, final String certificate) {
        final SecurityModeType securityMode = s.getAuth().getSecurityMode();
        String login = null;
        String password = null;
        String identity = null;
        String privateKey = null;
        String serverKey = null;
        String clientKey = null;
        String pemCertificate = null;
        String alias = null;

        switch (securityMode) {
            case PSK:
                final AuthPsk authPsk = (AuthPsk) s.getAuth();
                identity = authPsk.getIdentity();
                privateKey = authPsk.getPrivateKey();
                break;
            case X509:
                final AuthX509USP authX509 = (AuthX509USP) s.getAuth();
                pemCertificate = authX509.getPemCertificate();
                alias = authX509.getAlias();
                break;
            case BASIC:
                final AuthBasic authBasic = (AuthBasic) s.getAuth();
                login = authBasic.getLogin();
                password = authBasic.getPassword();
            case PUBLIC_KEY:
                final AuthPublicKey authPublicKey = (AuthPublicKey) s.getAuth();
                serverKey = authPublicKey.getServerKey();
                clientKey = authPublicKey.getClientKey();
                privateKey = authPublicKey.getPrivateKey();
        }
        final SecurityConfigurationDetailsWs detail = new SecurityConfigurationDetailsWs();
        detail.setId(s.getId());
        detail.setLogin(login);
        detail.setPassword(password);
        detail.setPrivateKey(privateKey);
        detail.setPskIdentity(identity);
        detail.setSecurityModeType(s.getAuth().getSecurityMode().name());
        detail.setServerCertChain(certificate);
        detail.setClientRpk(clientKey);
        detail.setServerRpk(serverKey);
        detail.setTrustedCertChain(pemCertificate);
        detail.setTrustedCertChainAlias(alias);
        detail.setUnderlyingProtocolType(s.getUnderlyingProtocol().name());
        return detail;
    }

    private void validateUps(final AddSecurityUSP usp) {
        if (uspRepository.existsByIdNotAndMaskLike(usp.getId(), usp.getMask())) {
            throw new FriendlyIllegalArgumentException(PARAMETER_NOT_UNIQUE, "Mask");
        }
    }

    private static String convertCertificate(final MultipartFile certificate) {
        if (certificate == null) {
            return null;
        }
        try {
            return new String(certificate.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
    }

    private Page<SecurityLwm2mEntity> getLwm2mSecurity(final String searchParam,
                                                                                       final Boolean searchExact,
                                                                                       final List<Integer> domainIds,
                                                                                       final ServerType serverType,
                                                                                       final Pageable p) {
        if (StringUtils.isBlank(searchParam)) {
            if (serverType.equals(ALL)) {
                return lwm2mRepository.findAllByDomainIdIn(domainIds, p);
            } else {
                return lwm2mRepository.findAllByDomainIdInAndServerType(domainIds, serverType, p);
            }
        } else {
            if (searchExact != null && searchExact) {
                if (serverType.equals(ALL)) {
                    return lwm2mRepository.findAllByDomainIdInAndMask(domainIds, searchParam, p);
                } else {
                    return lwm2mRepository.findAllByDomainIdInAndMaskAndServerType(domainIds, searchParam, serverType, p);
                }
            } else {
                if (serverType.equals(ALL)) {
                    return lwm2mRepository.findAllByDomainIdInAndMaskLike(domainIds, "%" + searchParam + "%", p);
                } else {
                    return lwm2mRepository.findAllByDomainIdInAndMaskLikeAndServerType(domainIds, "%" + searchParam + "%",
                            serverType, p);
                }
            }
        }

    }

    private Page<SecurityMqttEntity> getMqttSecurity(final String searchParam,
                                                                                     final Boolean searchExact,
                                                                                     final List<Integer> domainIds,
                                                                                     final Pageable p) {
        return StringUtils.isBlank(searchParam) ?
                mqttRepository.findAllByDomainIdIn(domainIds, p) :
                searchExact != null && searchExact
                        ? mqttRepository.findAllByDomainIdInAndMask(domainIds, searchParam, p)
                        : mqttRepository.findAllByDomainIdInAndMaskLike(domainIds, "%" + searchParam + "%", p);
    }

    private Page<SecurityUspEntity> getUspSecurity(final String searchParam,
                                                                                   final Boolean searchExact,
                                                                                   final List<Integer> domainIds,
                                                                                   final Pageable p) {
        return StringUtils.isBlank(searchParam) ?
                uspRepository.findAllByDomainIdIn(domainIds, p) :
                searchExact != null && searchExact
                        ? uspRepository.findAllByDomainIdInAndIdentifier(domainIds, searchParam, p)
                        : uspRepository.findAllByDomainIdInAndIdentifierLike(domainIds, "%" + searchParam + "%", p);
    }

    private SecurityLWM2M entityToLwm2mSecurity(final SecurityLwm2mEntity entity, final ClientType clientType,
                                                final String zoneId, final String dateFormat, final String timeFormat) {
        final SecurityModeType securityMode = getSecurityType(entity.getSecurityMode());
        return SecurityLWM2M.builder()
                .id(entity.getId())
                .mask(entity.getMask())
                .domainName(domainService.getDomainNameById(entity.getDomainId()))
                .serverType(entity.getServerType())
                .securityType(securityMode)
                .createdIso(DateTimeUtils.serverToUtc(entity.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId,
                        dateFormat, timeFormat))
                .identity(entity.getIdentity())
                .secretKey(entity.getSecretKey())
                .serverIdentity(entity.getPublicKey())
                .build();
    }

    private SecurityMQTT entityToMqttSecurity(final SecurityMqttEntity entity, final ClientType clientType,
                                              final String zoneId, final String dateFormat, final String timeFormat) {
        final MqttSecurityType securityType = getMqttSecurityType(entity.getSecurityType());
        return SecurityMQTT.builder()
                .id(entity.getId())
                .mask(entity.getMask())
                .domainName(domainService.getDomainNameById(entity.getDomainId()))
                .securityType(securityType)
                .createdIso(DateTimeUtils.serverToUtc(entity.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId,
                        dateFormat, timeFormat))
                .login(entity.getLogin())
                .password(entity.getPassword())
                .build();
    }

    private SecurityUSP entityToUspSecurity(final SecurityUspEntity entity) {
        return SecurityUSP.builder()
                .id(entity.getId())
                .mask(entity.getIdentifier())
                .domainName(domainService.getDomainNameById(entity.getDomainId()))
                .active(entity.getActive())
                .description(entity.getDescription())
                .identifierType(entity.getIdentifierType())
                .build();
    }

    private SecurityModeType getSecurityType(final Integer securityMode) {
        return securityMode.equals(0) ? PSK
                : securityMode.equals(1) ? SecurityModeType.BASIC
                : securityMode.equals(2) ? SecurityModeType.X_509
                : SecurityModeType.NO_SEC;
    }


    private Integer getSecurityType(final SecurityModeType securityMode) {
        return securityMode.equals(PSK) ? 0
                : securityMode.equals(SecurityModeType.BASIC) ? 1
                : securityMode.equals(X_509)  || securityMode.equals(X509) ? 2
                : 3;
    }

    public SecurityLWM2MDetails getLWM2MSecurityDetails(String token, SecurityDetailBody body) {
        jwtService.getSession(token);
        int id = body.getId();

        return lwm2mRepository.findById(id)
                .map(security -> {
                    SecurityModeType securityType = getSecurityType(security.getSecurityMode());

                    return new SecurityLWM2MDetails(
                            security.getId(),
                            security.getMaskType(),
                            security.getMask(),
                            security.getServerType(),
                            security.getDomainId(),
                            securityType,
                            security.getIdentity(),
                            security.getSecretKey(),
                            security.getPublicKey(),
                            security.getMasterSecret(),
                            security.getSenderId(),
                            security.getRecipientId(),
                            security.getAeadAlgorithmType(),
                            security.getHmacAlgorithmType(),
                            security.getMasterSalt(),
                            getIsOscore(security)
                    );
                })
                .orElse(null);
    }

    private Boolean getIsOscore(SecurityLwm2mEntity security) {
        return security.getSenderId() != null && !security.getSenderId().isEmpty()
                && security.getRecipientId() != null && !security.getRecipientId().isEmpty()
                && security.getMasterSecret() != null && !security.getMasterSecret().isEmpty()
                && security.getAeadAlgorithmType() != null
                && security.getHmacAlgorithmType() != null;
    }


    public SecurityMQTTDetails getMQTTSecurityDetails(String token, SecurityDetailBody body) {
        jwtService.getSession(token);
        int id = body.getId();

        return mqttRepository.findById(id)
                .map(security -> {
                    MqttSecurityType securityMode = getMqttSecurityType(security.getSecurityType());

                    return new SecurityMQTTDetails(
                            security.getId(),
                            security.getMaskType(),
                            security.getMask(),
                            security.getDomainId(),
                            securityMode,
                            security.getLogin(),
                            security.getPassword()
                    );
                })
                .orElse(null);
    }

    private MqttSecurityType getMqttSecurityType(Integer securityType) {
        return securityType == 0 ? MqttSecurityType.BASIC :
                securityType == 1 ? MqttSecurityType.NO_SEC :
                        null;
    }


    public SecurityLwm2mEntity editLWM2MSecurityDetails(String token, SecurityLWM2MDetails body) {
        final Session session = jwtService.getSession(token);
        final Integer id = body.getId();
        final ClientType clientType = session.getClientType();

            SecurityLwm2mEntity updatedEntity;
        String zoneId = session.getZoneId();
        if (id == null) {
                updatedEntity = new SecurityLwm2mEntity();
                updatedEntity.setCreated(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId));
                updatedEntity.setCreator(clientType.name().toUpperCase() + "/" + userService.getUser(session.getUserId(), zoneId).getName());
            } else {
                Optional<SecurityLwm2mEntity> entity = lwm2mRepository.findById(id);
                if (entity.isPresent()) {
                    updatedEntity = entity.get();
                } else {
                    return null;
                }
            }

            updatedEntity.setUpdated(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId));
            updatedEntity.setUpdater(clientType.name().toUpperCase() + "/" + userService.getUser(session.getUserId(), zoneId).getName());
            updatedEntity.setMask(body.getMask());
            updatedEntity.setMaskType(body.getMaskType());
            updatedEntity.setDomainId(body.getDomainId());
            updatedEntity.setServerType(body.getServerType());
            updatedEntity.setSecurityMode(getSecurityType(body.getSecurityType()));
            updatedEntity.setMasterSalt(body.getMasterSalt());
            updatedEntity.setMasterSecret(body.getMasterSecret());
            updatedEntity.setAeadAlgorithmType(body.getAeadAlgorithmType());
            updatedEntity.setHmacAlgorithmType(body.getHmacAlgorithmType());
            updatedEntity.setRecipientId(body.getRecipientId());
            updatedEntity.setSenderId(body.getSenderId());
            updatedEntity.setIdentity(body.getClientIdentity());
            updatedEntity.setPublicKey(body.getServerIdentity());
            updatedEntity.setSecretKey(body.getSecretKey());


            lwm2mRepository.saveAndFlush(updatedEntity);
            return updatedEntity;

    }

    public FTPage<AbstractSecurity> getMQTTSecurityPage(String token, MqttSecurityBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest
                (body.getPageNumbers(), body.getPageSize(), null, "id");
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final String zoneId = session.getZoneId();
        final Integer domainId = body.getDomainId();
        final String searchParam = body.getSearchParam();
        final Boolean searchExact = body.getSearchExact();

        final UserResponse user = userService.getUser(session.getUserId(), zoneId);
        final List<Integer> domainIds = domainId == null
                ? domainService.getChildDomainIds(user.getDomainId())
                : domainService.getChildDomainIds(domainId);

        final FTPage<AbstractSecurity> securityPage = new FTPage<>();
        final List<AbstractSecurity> securities;
        final FTPageDetails pageDetails;

        final List<Page<SecurityMqttEntity>> mqttSecurityEntities =
                pageable.stream()
                        .map(p -> getMqttSecurity(searchParam, searchExact, domainIds, p))
                        .collect(Collectors.toList());
        securities = mqttSecurityEntities.stream()
                .map(Page::getContent)
                .flatMap(entities -> entities.stream()
                        .map(e -> entityToMqttSecurity(e,
                                clientType,
                                zoneId,
                                user.getDateFormat(),
                                user.getTimeFormat())))
                .collect(Collectors.toList());
        pageDetails = PageUtils.buildPageDetails(mqttSecurityEntities);

        return securityPage.toBuilder()
                .items(securities)
                .pageDetails(pageDetails)
                .build();
    }




    public void deleteMQTTSecurity(String token, DeleteIotSecurityBody body) {
        jwtService.getSession(token);
        final List<Integer> ids = body.getIds();
        final List<SecurityMqttEntity> mqtt = mqttRepository.findAllById(ids);
        if (!mqtt.isEmpty()) {
            mqttRepository.deleteInBatch(mqtt);
        }
    }

    public SecurityMqttEntity editMQTTSecurityDetails(String token, SecurityMQTTDetails body) {
        final Session session = jwtService.getSession(token);
        final Integer id = body.getId();
        final ClientType clientType = session.getClientType();

        SecurityMqttEntity updatedEntity;
        String zoneId = session.getZoneId();
        if (id == null) {
            updatedEntity = new SecurityMqttEntity();
            updatedEntity.setCreated(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId));
            updatedEntity.setCreator(clientType.name().toUpperCase() + "/" + userService.getUser(session.getUserId(), zoneId).getName());
        } else {
            Optional<SecurityMqttEntity> entity = mqttRepository.findById(id);
            if (entity.isPresent()) {
                updatedEntity = entity.get();
            } else {
                return null;
            }
        }

        updatedEntity.setUpdated(DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId));
        updatedEntity.setUpdater(clientType.name().toUpperCase() + "/" + userService.getUser(session.getUserId(), zoneId).getName());
        updatedEntity.setMask(body.getMask());
        updatedEntity.setMaskType(body.getMaskType());
        updatedEntity.setDomainId(body.getDomainId());
        updatedEntity.setSecurityType(getMqttOrdinal(body.getSecurityType()));
        updatedEntity.setLogin(body.getLogin());
        updatedEntity.setPassword(body.getPassword());

        mqttRepository.saveAndFlush(updatedEntity);
        return updatedEntity;

    }

    private Integer getMqttOrdinal(MqttSecurityType securityType) {
        return securityType == MqttSecurityType.BASIC ? 0 :
                1;
    }


    public FTPage<AbstractSecurity> getUSPSecurityPage(String token, UspSecurityBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest
                (body.getPageNumbers(), body.getPageSize(), null, "id");
        final Session session = jwtService.getSession(token);
        final String zoneId = session.getZoneId();
        final Integer domainId = body.getDomainId();
        final String searchParam = body.getSearchParam();
        final Boolean searchExact = body.getSearchExact();

        final UserResponse user = userService.getUser(session.getUserId(), zoneId);
        final List<Integer> domainIds = domainId == null
                ? domainService.getChildDomainIds(user.getDomainId())
                : domainService.getChildDomainIds(domainId);

        final FTPage<AbstractSecurity> securityPage = new FTPage<>();
        final List<AbstractSecurity> securities;
        final FTPageDetails pageDetails;

        final List<Page<SecurityUspEntity>> uspSecurityEntities =
                pageable.stream()
                        .map(p -> getUspSecurity(searchParam, searchExact, domainIds, p))
                        .collect(Collectors.toList());
        securities = uspSecurityEntities.stream()
                .map(Page::getContent)
                .flatMap(entities -> entities.stream()
                        .map(this::entityToUspSecurity))
                .collect(Collectors.toList());
        pageDetails = PageUtils.buildPageDetails(uspSecurityEntities);

        return securityPage.toBuilder()
                .items(securities)
                .pageDetails(pageDetails)
                .build();

    }

    @Transactional
    public boolean deleteUSPSecurity(String token, DeleteIotSecurityBody body) {
        final Session session = jwtService.getSession(token);
        final List<Integer> ids = body.getIds();
        final boolean isDetail = false;
        if (isDetail) { //TODO: Condition 'isDetail' is always 'false'
            AcsProvider.getAcsWebService(session.getClientType()).deleteSecurityConfigurationDetails(ids);
        } else {
            AcsProvider.getAcsWebService(session.getClientType()).deleteSecurityConfiguration(ids);
        }
        return true;
    }

    public SecurityUSPDetails getUSPSecurityDetails(String token, SecurityDetailBody body) {
        jwtService.getSession(token);
        int id = body.getId();

        return uspRepository.findById(id)
                .map(security -> new SecurityUSPDetails(
                        security.getId(),
                        security.getIdentifier(),
                        security.getIdentifierType(),
                        security.getDescription(),
                        security.getActive(),
                        security.getDomainId(),
                        getSecurityUspMtpList(security)
                ))
                .orElse(null);
    }

    private List<SecurityUspMtp> getSecurityUspMtpList(SecurityUspEntity security) {
        return security.getSecurityUspMtpEntities() == null || security.getSecurityUspMtpEntities().isEmpty() ? Collections.emptyList() :
                security.getSecurityUspMtpEntities().stream()
                        .map(iotMapper::entityToSecurityUspMtp)
                        .collect(Collectors.toList());
    }

    @Transactional
    public boolean editUSPSecurityDetails(String token, SecurityUSPDetailsRequest body) {
        final Session session = jwtService.getSession(token);
        final Integer id = body.getId();

        SecurityUspEntity updatedEntity;

        if (id != null) {
            updatedEntity = uspRepository.findById(id)
                    .orElse(null);
            if (updatedEntity == null) {
                return false;
            }
        } else {
            updatedEntity = new SecurityUspEntity();
        }

        updatedEntity.setIdentifier(body.getIdentifier());
        updatedEntity.setIdentifierType(body.getIdentifierType());
        updatedEntity.setDomainId(body.getDomainId());
        updatedEntity.setActive(body.getActive());
        updatedEntity.setDescription(body.getDescription());
        updatedEntity.setProtocolId(5);
        uspMtpRepository.deleteAllBySecurityId(updatedEntity.getId());
        updatedEntity.setSecurityUspMtpEntities(mtpListToEntity(body.getSecurityUspMtpList(), updatedEntity));

        uspRepository.saveAndFlush(updatedEntity);
        return true;
    }

    private List<SecurityUspMtpEntity> mtpListToEntity(List<SecurityUspMtpRequest> securityUspMtpList, SecurityUspEntity entity) {
        return securityUspMtpList.stream()
                .map(security -> mtpToEntity(security, entity))
                .collect(Collectors.toList());
    }

    private SecurityUspMtpEntity mtpToEntity(SecurityUspMtpRequest security, SecurityUspEntity entity) {
        SecurityUspMtpEntity securityUspMtpEntity = new SecurityUspMtpEntity(entity, security.getMtpProtocolType(), security.getSecurityType(),
                security.getPskId(), security.getPskKey(), security.getClientRPK(), security.getServerRPK(),
                security.getLogin(), security.getPassword(), security.getCertificate(), security.getCustomAlias(),
                security.getPrivateRPK());
        securityUspMtpEntity.setId(security.getId());
        return securityUspMtpEntity;
    }

    public SecurityUspMtpModeResponse getAllModeTypesByMTP(final String token){
        jwtService.getSession(token);
        UnderlyingProtocolType[] types = UnderlyingProtocolType.values();
        List<SecurityUspMtpMode> mtpModes = Arrays.stream(types)
                .map(p -> new SecurityUspMtpMode(p, p.getSecurityTypes()))
                .collect(Collectors.toList());
        return new SecurityUspMtpModeResponse(mtpModes);
    }

    public UspIdentifierExists checkIfDetailExists(String token, BootstrapConfigDetailsExistBody body) {
        jwtService.getSession(token);
        String name = null;
        if(body.getId() != null) {
            Optional<SecurityUspEntity> entity = uspRepository.findById(body.getId());
            if (entity.isPresent()) {
                name = entity.get().getIdentifier();
            }
        }
        UspIdentifierExists ex = new UspIdentifierExists();
        ex.setExist(uspRepository.existsByIdentifier(body.getName()));
        if(body.getName().equals(name) && ex.isExist()) {
            ex.setExist(false);
        }
        return ex;
    }
}