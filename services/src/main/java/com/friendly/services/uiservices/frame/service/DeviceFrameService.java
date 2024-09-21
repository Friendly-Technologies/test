package com.friendly.services.uiservices.frame.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.*;
import com.friendly.commons.models.device.frame.DeviceMacFilter;
import com.friendly.commons.models.device.frame.DeviceStatusDetails;
import com.friendly.commons.models.device.frame.DeviceStatusResponse;
import com.friendly.commons.models.device.response.TaskIdsResponse;
import com.friendly.commons.models.device.setting.DeviceParameterUpdateRequest;
import com.friendly.commons.models.device.setting.Parameter;
import com.friendly.commons.models.tabs.DeviceTab;
import com.friendly.commons.models.user.Session;
import com.friendly.services.device.info.service.DeviceSettingService;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.info.orm.acs.model.DeviceInfoEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.history.orm.acs.repository.DeviceHistoryRepository;
import com.friendly.services.device.info.orm.acs.repository.DeviceInfoRepository;
import com.friendly.services.device.activity.orm.acs.repository.TaskRepository;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.userinterface.InterfaceService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.ftacs.BooleanResponse;
import com.ftacs.Exception_Exception;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.friendly.commons.models.device.DeviceStatusType.*;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Service that exposes the base functionality for interacting with {@link Device} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceFrameService {

    @NonNull
    private final DeviceSettingService settingService;

    @NonNull
    private final InterfaceService interfaceService;

    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final DeviceHistoryRepository deviceHistoryRepository;

    @NonNull
    private final TaskRepository taskRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final DeviceInfoRepository deviceInfoRepository;

    @NonNull
    final ParameterService parameterService;


    public DeviceMacFilter getMacFilter(final String token, final Long deviceId) {
        jwtService.getSession(token);

        final List<String> enableParams =
                parameterService.getParamValuesLike(deviceId,
                        "InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.MACAddressControlEnabled");

        if (enableParams.isEmpty()) {
            return null;
        }

        final String param = enableParams.get(0);
        final Boolean enable = param.equals("1") || param.equals("true") ? TRUE : FALSE;
        final List<String> macAddresses = new ArrayList<>();

        if (enable) {
            parameterService.getParamValuesLike(deviceId,
                            "InternetGatewayDevice.LANDevice.%.LANHostConfigManagement.AllowedMACAddresses")
                    .forEach(p -> macAddresses.addAll(Arrays.stream(p.split(","))
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.toList())));
        }

        return DeviceMacFilter.builder()
                .enable(enable)
                .macAddresses(macAddresses)
                .build();
    }

    public TaskIdsResponse updateMacFilter(final String token, final MacFilterBody body) {
        final Session session = jwtService.getSession(token);
        final Long deviceId = body.getDeviceId();
        final DeviceMacFilter filter = body.getMacFilter();

        final String enableParamName =
                parameterService.getParamNamesLike(deviceId,
                                "InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.MACAddressControlEnabled")
                        .stream()
                        .sorted()
                        .findFirst()
                        .orElseThrow(() -> new FriendlyIllegalArgumentException(DEVICE_PARAMETER_NOT_FOUND,
                                "MACAddressControlEnabled"));
        final Parameter enableParam = Parameter.builder()
                .fullName(enableParamName)
                .value(filter.getEnable() ? "1" : "0")
                .build();
        final char instance = enableParamName.charAt(32);
        final String macParamName = "InternetGatewayDevice.LANDevice.%.LANHostConfigManagement.AllowedMACAddresses"
                .replace('%', instance);
        final Parameter macParam = Parameter.builder()
                .fullName(macParamName)
                .value(String.join(",", filter.getMacAddresses()))
                .build();
        final DeviceParameterUpdateRequest parameterUpdateRequest =
                DeviceParameterUpdateRequest.builder()
                        .push(false)
                        .reprovision(false)
                        .parameters(Arrays.asList(enableParam, macParam))
                        .build();

        return new TaskIdsResponse(settingService.updateDeviceParams(deviceId, parameterUpdateRequest,
                session, false, token));
    }

    public DeviceStatusResponse getDeviceConnectionStatus(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        final Integer deviceStatusTimeout = interfaceService.getInterfaceValue(clientType, "DeviceStatusTimeout")
                .map(Integer::parseInt)
                .orElse(10);

        try {
            if (!cpeRepository.existsById(deviceId)) {
                throw new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND);
            }
            final BooleanResponse status = AcsProvider.getAcsWebService(clientType)
                    .push(deviceId.intValue(), deviceStatusTimeout);

            DeviceStatusType statusType = status.isResult() ? online : offline;
            if (statusType == offline) {
                Optional<DeviceInfoEntity> optDeviceInfoEntity = deviceInfoRepository.findById(deviceId);
                DeviceInfoEntity deviceInfoEntity = optDeviceInfoEntity.orElse(null);
                if (deviceInfoEntity != null
                        && deviceInfoEntity.getProtocolId() == ProtocolType.LWM2M.ordinal()) {
                    switch (deviceInfoEntity.getIsOnline()) {
                        case 0:
                            statusType = offline;
                            break;
                        case 1:
                            statusType = online;
                            break;
                        case 2:
                            statusType = onlineLimited;
                            break;
                        default:
                            statusType = error;
                            break;
                    }
                } else {
                    Instant client = Instant.now().plusSeconds(-20);
                    Instant date = DateTimeUtils.utcToServer(client, session.getClientType());
                    Integer cnt = cpeRepository.checkCpeInNextSession(date, deviceId);
                    statusType = cnt > 0 ? onlineLimited : offline;
                }
            }
            return DeviceStatusResponse.builder()
                    .status(statusType)
                    .build();
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public DeviceStatusDetails getDeviceStatusDetails(final String token, final Long deviceId) {
        jwtService.getSession(token);

        final Instant from = Instant.now().minus(1, DAYS);
        ProtocolType protocolType = ProtocolType.fromValue(cpeRepository.getProtocolTypeByDevice(deviceId).orElse(0));
        return DeviceStatusDetails.builder()
                .connectFailure(deviceHistoryRepository.getConnectivityFailureCount(deviceId, from))
                .rebootAmount(protocolType.equals(ProtocolType.LWM2M) ?
                        taskRepository.getCompletedTasksOfTypeAmount(deviceId, from, FTTaskTypesEnum.Reboot.getCode()) :
                        deviceHistoryRepository.getRebootAmount(deviceId, from))
                .build();
    }


    public List<DeviceTab> getWirelessTabs(final Long deviceId) {
        if (parameterService.isParamExistLike(deviceId, "InternetGatewayDevice.%")) {
            final List<DeviceTab> tabs = new ArrayList<>();
            final List<String> channel =
                    parameterService.getParamValuesLike(deviceId,
                            "InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.Channel");
            if (!channel.isEmpty()) {
                channel.stream()
                        .filter(NumberUtils::isCreatable)
                        .forEach(c -> setWiFiTabs(tabs, c));
            } else {
                final List<String> possibleChannels =
                        parameterService.getParamValuesLike(deviceId,
                                "InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.PossibleChannels");
                if (!possibleChannels.isEmpty()) {
                    possibleChannels
                            .stream()
                            .map(v -> Arrays.stream(v.split(","))
                                    .findFirst())
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .forEach(v -> {
                                if (NumberUtils.isCreatable(v)) {
                                    setWiFiTabs(tabs, v);
                                } else {
                                    Arrays.stream(v.split("-"))
                                            .findFirst()
                                            .ifPresent(c -> setWiFiTabs(tabs, c));
                                }
                            });
                } else {
                    final List<String> standard =
                            parameterService.getParamValuesLike(deviceId,
                                    "InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.Standard");
                    if (standard.contains("b") || standard.contains("g") || standard.contains("n")) {
                        tabs.add(getWifi24());
                    } else if (standard.contains("a")) {
                        tabs.add(getWifi5());
                    }
                }
            }

            return tabs.stream().distinct().filter(Objects::nonNull).collect(Collectors.toList());

        } else if (parameterService.isParamExistLike(deviceId, "Device.%")) {
            final List<DeviceTab> tabs = new ArrayList<>();
            final List<String> channel = parameterService.getParamValuesLike(deviceId, "Device.WiFi.Radio.%.Channel");
            if (!channel.isEmpty()) {
                channel.stream()
                        .filter(NumberUtils::isCreatable)
                        .forEach(c -> setWiFiTabs(tabs, c));
            } else {
                final List<String> bands =
                        parameterService.getParamValuesLike(deviceId, "Device.WiFi.Radio.%.OperatingFrequencyBand");
                if (!bands.isEmpty()) {
                    if (bands.contains("2")) {
                        tabs.add(getWifi24());
                    } else if (bands.contains("5")) {
                        tabs.add(getWifi5());
                    }
                } else {
                    final List<String> standard =
                            parameterService.getParamValuesLike(deviceId, "Device.WiFi.Radio.%.OperatingStandards");
                    if (standard.contains("b") || standard.contains("g") || standard.contains("n")) {
                        tabs.add(getWifi24());
                    } else if (standard.contains("a")) {
                        tabs.add(getWifi5());
                    }
                }
            }

            if (parameterService.isParamExist(deviceId, "Device.X_TP_MESH_WIFI.MeshMode")
                    || parameterService.isParamExistLike(deviceId, "Device.WiFi.MultiAP.APDevice.%.BackhaulLinkType")) {
                tabs.add(getMesh());
            }

            return tabs.stream().distinct().filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private void setWiFiTabs(final List<DeviceTab> tabs, final String value) {
        final int channel = Integer.parseInt(value);
        if (channel < 15) {
            tabs.add(getWifi24());
        } else {
            tabs.add(getWifi5());
        }
    }

    private DeviceTab getWifi24() {
        return getWifiTab("WiFi 2.4GHz", "2");
    }

    private DeviceTab getWifi5() {
        return getWifiTab("WiFi 5GHz", "5");
    }

    private DeviceTab getMesh() {
        return getWifiTab("Mesh", "mesh");
    }

    private DeviceTab getWifiTab(final String name, final String path) {
        return DeviceTab.builder()
                .name(name)
                .path(path)
                .build();
    }

}
