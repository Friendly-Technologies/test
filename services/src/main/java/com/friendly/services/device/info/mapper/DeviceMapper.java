package com.friendly.services.device.info.mapper;

import com.friendly.commons.cache.CpeParameterNameCache;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AccountInfo;
import com.friendly.commons.models.device.Device;
import com.friendly.commons.models.device.DeviceHistory;
import com.friendly.commons.models.device.DeviceHistoryDetails;
import com.friendly.commons.models.device.DeviceInfo;
import com.friendly.commons.models.device.DeviceStatusType;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.TaskList;
import com.friendly.commons.models.device.file.DeliveryMethodType;
import com.friendly.commons.models.device.file.DeliveryProtocolType;
import com.friendly.commons.models.device.file.DeviceFile;
import com.friendly.commons.models.device.provision.ProvisionDownload;
import com.friendly.commons.models.device.provision.ProvisionObject;
import com.friendly.commons.models.device.provision.ProvisionParameter;
import com.friendly.commons.models.device.provision.ProvisionRpc;
import com.friendly.commons.models.device.rpc.CustomRpc;
import com.friendly.commons.models.device.setting.DeviceObjectSimple;
import com.friendly.commons.models.device.setting.DeviceParameterSimple;
import com.friendly.commons.models.device.setting.DeviceTabView;
import com.friendly.commons.models.device.setting.TabViewType;
import com.friendly.commons.models.reports.DeviceXml;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.activity.orm.acs.model.DeviceActivityDetailsEntity;
import com.friendly.services.device.history.orm.acs.model.DeviceHistoryDetailsProjection;
import com.friendly.services.device.history.orm.acs.model.DeviceHistoryEntity;
import com.friendly.services.device.history.orm.acs.repository.DeviceHistoryRepository;
import com.friendly.services.device.info.model.TaskParam;
import com.friendly.services.device.info.orm.acs.model.CpeEntity;
import com.friendly.services.device.info.orm.acs.model.CustomDeviceEntity;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.device.info.orm.acs.model.DeviceInfoEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterEntity;
import com.friendly.services.device.parameterstree.orm.acs.repository.DeviceParameterRepository;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionEntity;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionFileEntity;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionObjectEntity;
import com.friendly.services.device.provision.orm.acs.model.DeviceRpcEntity;
import com.friendly.services.filemanagement.orm.acs.model.DeviceFileDownloadEntity;
import com.friendly.services.filemanagement.orm.acs.model.DeviceFileUploadEntity;
import com.friendly.services.productclass.orm.acs.model.ManufacturerEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceDetailsLwm2mRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceLwm2mRepository;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.ftacs.CustDeviceCustomFieldListWS;
import com.ftacs.CustDeviceCustomFieldWS;
import com.ftacs.CustDeviceWS;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.friendly.commons.models.device.file.DeliveryMethodType.NotSet;
import static com.friendly.commons.models.device.file.DeliveryMethodType.Pull;
import static com.friendly.commons.models.device.file.DeliveryMethodType.Push;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPS;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPoverTCP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPoverTLS;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.HTTP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.HTTPS;
import static com.friendly.commons.models.device.provision.ProvisionType.DOWNLOAD;
import static com.friendly.commons.models.device.provision.ProvisionType.OBJECTS;
import static com.friendly.commons.models.device.provision.ProvisionType.PARAMETERS;
import static com.friendly.commons.models.device.provision.ProvisionType.RPC;
import static com.friendly.services.device.parameterstree.utils.ParameterUtil.getCustParamsMap;
import static com.friendly.services.device.parameterstree.utils.ParameterUtil.getIpAddress;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component
@RequiredArgsConstructor
public class DeviceMapper {

    private final DeviceParameterRepository deviceParameterRepository;
    @NonNull
    CpeParameterNameCache cpeParameterNameCache;
    @NonNull
    private final CpeRepository cpeRepository;
    @NonNull
    private final ResourceDetailsLwm2mRepository resourceDetailsLwm2mRepository;
    @NonNull
    private final ResourceLwm2mRepository resourceLwm2mRepository;
    @NonNull
    private final ParameterService parameterService;

    @NonNull
    private final ParameterNameService parameterNameService;
    private final DeviceHistoryRepository deviceHistoryRepository;

    public Device deviceEntityToDevice(final DeviceEntity entity, final ClientType clientType,
                                       final String zoneId, final String dateFormat,
                                       final String timeFormat) {

        final Optional<CustomDeviceEntity> customDevice = Optional.ofNullable(entity.getCustomDevice());
        return Device.builder()
                .id(entity.getId())
                .created(DateTimeUtils.formatAcs(
                        entity.getCreated(), clientType, zoneId, dateFormat, timeFormat))
                .createdIso(DateTimeUtils.serverToUtc(entity.getCreated(), clientType))
                .status(entity.getIsOnline() == null || entity.getIsOnline() == 0
                        ? 0 : 1)
                .serial(entity.getSerial())
                .updated(DateTimeUtils.formatAcs(
                        entity.getUpdated(), clientType, zoneId, dateFormat, timeFormat))
                .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                .firmware(entity.getFirmware())
                .protocolType(ProtocolType.fromValue(entity.getProtocolId() == null ? 0 : entity.getProtocolId()))
                .domainName(entity.getDomainName())
                .manufacturer(Optional.ofNullable(entity.getProductClass())
                        .map(ProductClassEntity::getProductGroup)
                        .map(ProductClassGroupEntity::getManufacturerName)
                        .orElse(null))
                .model(Optional.ofNullable(entity.getProductClass())
                        .map(ProductClassEntity::getProductGroup)
                        .map(ProductClassGroupEntity::getModel)
                        .orElse(null))
                .oui(Optional.ofNullable(entity.getProductClass())
                        .map(ProductClassEntity::getManufacturer)
                        .map(ManufacturerEntity::getOui)
                        .orElse(null))
                .phone(customDevice.map(CustomDeviceEntity::getPhone).orElse(null))
                .userLogin(customDevice.map(CustomDeviceEntity::getUserLogin).orElse(null))
                .userName(customDevice.map(CustomDeviceEntity::getUserName).orElse(null))
                .zip(customDevice.map(CustomDeviceEntity::getZip).orElse(null))
                .userLocation(customDevice.map(CustomDeviceEntity::getUserLocation).orElse(null))
                .userTag(customDevice.map(CustomDeviceEntity::getUserTag).orElse(null))
                .userStatus(customDevice.map(CustomDeviceEntity::getUserStatus).orElse(null))
                .userId(customDevice.map(CustomDeviceEntity::getUserId).orElse(null))
                .cust1(customDevice.map(CustomDeviceEntity::getCust1).orElse(null))
                .cust2(customDevice.map(CustomDeviceEntity::getCust2).orElse(null))
                .cust3(customDevice.map(CustomDeviceEntity::getCust3).orElse(null))
                .cust4(customDevice.map(CustomDeviceEntity::getCust4).orElse(null))
                .cust5(customDevice.map(CustomDeviceEntity::getCust5).orElse(null))
                .cust6(customDevice.map(CustomDeviceEntity::getCust6).orElse(null))
                .cust7(customDevice.map(CustomDeviceEntity::getCust7).orElse(null))
                .cust8(customDevice.map(CustomDeviceEntity::getCust8).orElse(null))
                .cust9(customDevice.map(CustomDeviceEntity::getCust9).orElse(null))
                .cust10(customDevice.map(CustomDeviceEntity::getCust10).orElse(null))
                .completedTasks(entity.getCompletedTasks())
                .failedTasks(entity.getFailedTasks())
                .rejectedTasks(entity.getRejectedTasks())
                .pendingTasks(entity.getPendingTasks())
                .sentTasks(entity.getSentTasks() != null ? entity.getSentTasks() : 0)
                .build();
    }

    public Device deviceEntityToDevice(final Session session, final UserResponse user,
                                        final DeviceEntity deviceEntity, Map<Long, Map<String, String>> cpeIdToNameValue,
                                       final ClientType clientType) {
        final Device device = deviceEntityToDevice(deviceEntity, session.getClientType(),
                session.getZoneId(), user.getDateFormat(),
                user.getTimeFormat());
        final Long deviceId = device.getId();
        if (!cpeIdToNameValue.containsKey(deviceId)) {
            return device;
        }
        Map<String, String> nameValueMap = cpeIdToNameValue.get(deviceId);
        String acsUsername = getCustParamsMap("acsUsername", nameValueMap, clientType);
        return device.toBuilder()
                .hardware(getCustParamsMap("hardware", nameValueMap, clientType))
                .software(getCustParamsMap("software", nameValueMap, clientType))
                .ipAddress(getIpAddress(cpeIdToNameValue.get(deviceId), clientType))
                .macAddress(parameterService.getMacAddress(null, nameValueMap))
                .uptime(getCustParamsMap("uptime", nameValueMap, clientType))
                .acsUsername(acsUsername.isEmpty() || acsUsername.equals("null") ? null : acsUsername)
                .build();
    }

    public DeviceXml deviceEntityToDeviceXml(final DeviceEntity entity) {
        final Optional<ProductClassGroupEntity> productGroup = Optional.ofNullable(entity.getProductClass())
                .map(ProductClassEntity::getProductGroup);
        return DeviceXml.builder()
                .serial(entity.getSerial())
                .manufacturer(productGroup.map(ProductClassGroupEntity::getManufacturerName)
                        .orElse(null))
                .model(productGroup.map(ProductClassGroupEntity::getModel)
                        .orElse(null))
                .build();
    }

    public DeviceInfo deviceEntityToDeviceInfo(final DeviceInfoEntity entity, final ClientType clientType,
                                               final String zoneId, final String dateFormat,
                                               final String timeFormat) {
        return DeviceInfo.builder()
                .id(entity.getId())
                .created(DateTimeUtils.formatAcs(
                        entity.getCreated(), clientType, zoneId, dateFormat, timeFormat))
                .createdIso(DateTimeUtils.serverToUtc(entity.getCreated(), clientType))
                .updated(DateTimeUtils.formatAcs(
                        entity.getUpdated(), clientType, zoneId, dateFormat, timeFormat))
                .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                .status(entity.getIsOnline() == null || entity.getIsOnline() == 0
                        ? DeviceStatusType.offline : DeviceStatusType.online)
                .serial(entity.getSerial())
                .firmware(entity.getFirmware())
                .manufacturer(Optional.ofNullable(entity.getProductClass())
                        .map(ProductClassEntity::getProductGroup)
                        .map(ProductClassGroupEntity::getManufacturerName)
                        .orElse(null))
                .model(Optional.ofNullable(entity.getProductClass())
                        .map(ProductClassEntity::getProductGroup)
                        .map(ProductClassGroupEntity::getModel)
                        .orElse(null))
                .oui(Optional.ofNullable(entity.getProductClass())
                        .map(ProductClassEntity::getManufacturer)
                        .map(ManufacturerEntity::getOui)
                        .orElse(null))
                .protocolType(ProtocolType.fromValue(entity.getProtocolId() == null ? 0 : entity.getProtocolId()))
                .build();
    }

    public DeviceInfo deviceEntityToDeviceInfo(final DeviceInfoEntity deviceInfoEntity, final Session session,
                                                final UserResponse user) {
        final DeviceInfo device = deviceEntityToDeviceInfo(deviceInfoEntity, session.getClientType(),
                session.getZoneId(), user.getDateFormat(),
                user.getTimeFormat());
        final ClientType clientType = session.getClientType();
        final String protocolVersion;
        final String batteryLevel;
        if (device.getProtocolType() == ProtocolType.TR069) {
            String root = parameterService.getRootParamName(device.getId());
            protocolVersion = root.equals("InternetGatewayDevice.") ? "TR098" : "TR181";
        } else {
            protocolVersion = null;
        }
        if (device.getProtocolType() == ProtocolType.LWM2M) {
            batteryLevel = parameterService.getParamValue(device.getId(), "Root.Device.0.Battery Level");
        } else {
            batteryLevel = null;
        }
        Map<String, String> nameValueMap = parameterService.findDevicePropertiesNameValuesMap(device.getId());
        if (nameValueMap == null || nameValueMap.isEmpty()) {
            return device;
        }
        return device.toBuilder()
                .protocolVersion(protocolVersion)
                .hardware(getCustParamsMap( "hardware", nameValueMap, clientType))
                .ipAddress(getIpAddressForDeviceInfo(device.getId()))
                .batteryLevel(batteryLevel)
                .macAddress(getMacAddressForDeviceInfo(device.getId()))
                .uptime(getCustParamsMap("uptime", nameValueMap, clientType))
                .build();
    }

    private String getMacAddressForDeviceInfo(Long deviceId) {
        String activeConnection = parameterService.readActiveConnection(deviceId);
        if(activeConnection == null) {
            return null;
        }
        Integer paramId = cpeParameterNameCache.getIdByName(activeConnection
                + "MACAddress");
        if(paramId == null) {
            return null;
        }
        Optional<CpeParameterEntity> param
                = deviceParameterRepository.findByCpeIdAndNameId(deviceId,
                paramId.longValue());

        return param.map(CpeParameterEntity::getValue).orElse(null);
    }

    private String getIpAddressForDeviceInfo(final Long deviceId) {

        String rootParamName = parameterService.getRootParamName(deviceId);
        if(rootParamName == null) {
            return null;
        }
        Integer paramId = cpeParameterNameCache.getIdByName(rootParamName
                + "ManagementServer.ConnectionRequestURL");
        if(paramId == null) {
            return null;
        }
        Optional<CpeParameterEntity> param
                = deviceParameterRepository.findByCpeIdAndNameId(deviceId,
                paramId.longValue());

        return param.map(cpeParameterEntity -> extractIp(cpeParameterEntity.getValue())).orElse(null);
    }

    private String extractIp(String url) {
        if(url == null) {
            return null;
        }
        String cleanUrl = url.replaceFirst("^([a-zA-Z]+://)", "");
        int endIndex = cleanUrl.indexOf(':');
        return (endIndex != -1) ? cleanUrl.substring(0, endIndex) : cleanUrl;
    }

    public CustDeviceWS accountInfoToCustDeviceWS(final String serial, final AccountInfo info) {
        final CustDeviceWS custDeviceWS = new CustDeviceWS();
        final CustDeviceCustomFieldListWS customFieldsWS = new CustDeviceCustomFieldListWS();
        final CustDeviceCustomFieldWS cust1 = new CustDeviceCustomFieldWS();
        cust1.setName("cust1");
        cust1.setValue(info.getCust1());
        final CustDeviceCustomFieldWS cust2 = new CustDeviceCustomFieldWS();
        cust2.setName("cust2");
        cust2.setValue(info.getCust2());
        final CustDeviceCustomFieldWS cust3 = new CustDeviceCustomFieldWS();
        cust3.setName("cust3");
        cust3.setValue(info.getCust3());
        final CustDeviceCustomFieldWS cust4 = new CustDeviceCustomFieldWS();
        cust4.setName("cust4");
        cust4.setValue(info.getCust4());
        final CustDeviceCustomFieldWS cust5 = new CustDeviceCustomFieldWS();
        cust5.setName("cust5");
        cust5.setValue(info.getCust5());
        final CustDeviceCustomFieldWS cust6 = new CustDeviceCustomFieldWS();
        cust6.setName("cust6");
        cust6.setValue(info.getCust6());
        final CustDeviceCustomFieldWS cust7 = new CustDeviceCustomFieldWS();
        cust7.setName("cust7");
        cust7.setValue(info.getCust7());
        final CustDeviceCustomFieldWS cust8 = new CustDeviceCustomFieldWS();
        cust8.setName("cust8");
        cust8.setValue(info.getCust8());
        final CustDeviceCustomFieldWS cust9 = new CustDeviceCustomFieldWS();
        cust9.setName("cust9");
        cust9.setValue(info.getCust9());
        final CustDeviceCustomFieldWS cust10 = new CustDeviceCustomFieldWS();
        cust10.setName("cust10");
        cust10.setValue(info.getCust10());
        final CustDeviceCustomFieldWS cust11 = new CustDeviceCustomFieldWS();
        cust11.setName("cust11");
        cust11.setValue(info.getCust11());
        final CustDeviceCustomFieldWS cust12 = new CustDeviceCustomFieldWS();
        cust12.setName("cust12");
        cust12.setValue(info.getCust12());
        final CustDeviceCustomFieldWS cust13 = new CustDeviceCustomFieldWS();
        cust13.setName("cust13");
        cust13.setValue(info.getCust13());
        final CustDeviceCustomFieldWS cust14 = new CustDeviceCustomFieldWS();
        cust14.setName("cust14");
        cust14.setValue(info.getCust14());
        final CustDeviceCustomFieldWS cust15 = new CustDeviceCustomFieldWS();
        cust15.setName("cust15");
        cust15.setValue(info.getCust15());
        final CustDeviceCustomFieldWS cust16 = new CustDeviceCustomFieldWS();
        cust16.setName("cust16");
        cust16.setValue(info.getCust16());
        final CustDeviceCustomFieldWS cust17 = new CustDeviceCustomFieldWS();
        cust17.setName("cust17");
        cust17.setValue(info.getCust17());
        final CustDeviceCustomFieldWS cust18 = new CustDeviceCustomFieldWS();
        cust18.setName("cust18");
        cust18.setValue(info.getCust18());
        final CustDeviceCustomFieldWS cust19 = new CustDeviceCustomFieldWS();
        cust19.setName("cust19");
        cust19.setValue(info.getCust19());
        final CustDeviceCustomFieldWS cust20 = new CustDeviceCustomFieldWS();
        cust20.setName("cust20");
        cust20.setValue(info.getCust20());

        customFieldsWS.getFields().addAll(Arrays.asList(
                cust1, cust2, cust3, cust4, cust5, cust6, cust7, cust8, cust9, cust10,
                cust11, cust12, cust13, cust14, cust15, cust16, cust17, cust18, cust19, cust20));
        custDeviceWS.setSerial(serial);
        custDeviceWS.setLoginName(info.getUserLogin());
        custDeviceWS.setName(info.getUserName());
        custDeviceWS.setZip(info.getZip());
        custDeviceWS.setTelephone(info.getPhone());
        custDeviceWS.setLocation(info.getUserLocation());
        custDeviceWS.setUserTag(info.getUserTag());
        custDeviceWS.setUserId(info.getUserId());
        custDeviceWS.setUserStatus(info.getUserStatus());
        custDeviceWS.setCustomFields(customFieldsWS);
        custDeviceWS.setLatitude(info.getLatitude());
        custDeviceWS.setLongitude(info.getLongitude());
        return custDeviceWS;
    }

    public AccountInfo customDeviceToSubscriberInfo(final CustomDeviceEntity info, final CpeEntity cpeEntity) {
        return AccountInfo.builder()
                .domainId(cpeEntity.getDomainId())
                .domainName(cpeEntity.getDomainName())
                .userLogin(getValueOrEmptyString(info.getUserLogin()))
                .userName(getValueOrEmptyString(info.getUserName()))
                .zip(getValueOrEmptyString(info.getZip()))
                .phone(getValueOrEmptyString(info.getPhone()))
                .userLocation(getValueOrEmptyString(info.getUserLocation()))
                .userTag(getValueOrEmptyString(info.getUserTag()))
                .userId(getValueOrEmptyString(info.getUserId()))
                .userStatus(getValueOrEmptyString(info.getUserStatus()))
                .cust1(getValueOrEmptyString(info.getCust1()))
                .cust2(getValueOrEmptyString(info.getCust2()))
                .cust3(getValueOrEmptyString(info.getCust3()))
                .cust4(getValueOrEmptyString(info.getCust4()))
                .cust5(getValueOrEmptyString(info.getCust5()))
                .cust6(getValueOrEmptyString(info.getCust6()))
                .cust7(getValueOrEmptyString(info.getCust7()))
                .cust8(getValueOrEmptyString(info.getCust8()))
                .cust9(getValueOrEmptyString(info.getCust9()))
                .cust10(getValueOrEmptyString(info.getCust10()))
                .cust11(getValueOrEmptyString(info.getCust11()))
                .cust12(getValueOrEmptyString(info.getCust12()))
                .cust13(getValueOrEmptyString(info.getCust13()))
                .cust14(getValueOrEmptyString(info.getCust14()))
                .cust15(getValueOrEmptyString(info.getCust15()))
                .cust16(getValueOrEmptyString(info.getCust16()))
                .cust17(getValueOrEmptyString(info.getCust17()))
                .cust18(getValueOrEmptyString(info.getCust18()))
                .cust19(getValueOrEmptyString(info.getCust19()))
                .cust20(getValueOrEmptyString(info.getCust20()))
                .latitude(info.getLatitude())
                .longitude(info.getLongitude())
                .build();
    }

    private String getValueOrEmptyString(String param) {
        return param != null ? param : Strings.EMPTY;
    }

    public TaskList objectToTaskList(final Object[] tasks) {
        return TaskList.builder()
                .completedTasks(Math.toIntExact((Long) tasks[0]))
                .failedTasks(Math.toIntExact((Long) tasks[1]))
                .rejectedTasks(Math.toIntExact((Long) tasks[2]))
                .pendingTasks(Math.toIntExact((Long) tasks[3]))
                .sentTasks(Math.toIntExact((Long) tasks[4]))
                .build();
    }

    public List<DeviceHistory> deviceHistoryEntitiesToDeviceHistories(final List<DeviceHistoryEntity> entities,
                                                                      final ClientType clientType,
                                                                      final String dateFormat,
                                                                      final String timeFormat) {
        return entities.stream()
                .map(h -> deviceHistoryEntityToDeviceHistory(h, clientType, dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    private DeviceHistory deviceHistoryEntityToDeviceHistory(final DeviceHistoryEntity entity,
                                                             final ClientType clientType,
                                                             final String dateFormat,
                                                             final String timeFormat) {
        Integer itemsCount = deviceHistoryRepository.getItemsCount(entity.getId());
        return DeviceHistory.builder()
                .id(entity.getId())
                .activityType(entity.getActivityType())
                .createdIso(entity.getCreated())
                .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, null,
                        dateFormat, timeFormat))
                .itemsCount(entity.getActivityType().equals("4 VALUE CHANGE") ? itemsCount : null)
                .build();
    }

    public DeviceFile deviceFileUploadEntityToDeviceFile(final DeviceFileUploadEntity uploadEntity,
                                                         final ClientType clientType, final String dateFormat,
                                                         final String timeFormat, String httpServer,
                                                         final String zoneId) {
        String link = uploadEntity.getUrl();
        String url = link == null || !link.contains("/") ? link : link.substring(0, link.lastIndexOf("/") + 1);
        String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);

        return DeviceFile.builder()
                .id(uploadEntity.getId())
                .state(uploadEntity.getState() == null
                        ? "No info" : uploadEntity.getState())
                .description(uploadEntity.getFileName())
                .fileType(uploadEntity.getFileType())
                .url(url)
                .link(link)
                .fileName(fileName)
                .isManual(url != null && !url.contains(httpServer))
                .createdIso(DateTimeUtils.serverToUtc(uploadEntity.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(uploadEntity.getCreated(), clientType, null,
                        dateFormat, timeFormat))
                .completedIso(uploadEntity.getCompleted() == null ? null
                        : DateTimeUtils.serverToClient(uploadEntity.getCompleted(), clientType, zoneId))
                .completed(uploadEntity.getCompleted() == null ? null
                        : DateTimeUtils.formatAcs(uploadEntity.getCompleted(), clientType, null,
                        dateFormat, timeFormat))
                .creator(StringUtils.substringAfter(uploadEntity.getCreator(), "/"))
                .application(StringUtils.substringBefore(uploadEntity.getCreator(), "/"))
                .build();
    }

    public DeviceFile deviceFileDownloadEntityToDeviceFile(final DeviceFileDownloadEntity downloadEntity,
                                                           final ClientType clientType, final String dateFormat,
                                                           final String timeFormat, String httpServer,
                                                           final String zoneId) {
        String link = downloadEntity.getUrl();
        String url = link == null || !link.contains("/") ? link : link.substring(0, link.lastIndexOf("/") + 1);
        String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);

        return DeviceFile.builder()
                .id(downloadEntity.getId())
                .state(downloadEntity.getState() == null
                        ? "No info" : downloadEntity.getState())
                .description(downloadEntity.getFileName())
                .url(url)
                .link(link)
                .fileName(fileName)
                .isManual(url != null && httpServer != null && !url.contains(httpServer))
                .fileType(downloadEntity.getFileType())
                .createdIso(DateTimeUtils.serverToUtc(downloadEntity.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(downloadEntity.getCreated(), clientType, null,
                        dateFormat, timeFormat))
                .completedIso(downloadEntity.getCompleted() == null ? null
                        : DateTimeUtils.serverToClient(downloadEntity.getCompleted(), clientType, zoneId))
                .completed(downloadEntity.getCompleted() == null ? null
                        : DateTimeUtils.formatAcs(downloadEntity.getCompleted(), clientType, null,
                        dateFormat, timeFormat))
                .creator(StringUtils.substringAfter(downloadEntity.getCreator(), "/"))
                .application(StringUtils.substringBefore(downloadEntity.getCreator(), "/"))
                .targetFileName(downloadEntity.getTargetFileName())
                .build();
    }

    public Integer deliveryMethodToInteger(final DeliveryMethodType method) {
        switch (method) {
            case NotSet:
                return -1;
            case Pull:
                return 0;
            case Push:
                return 1;
            default:
                return null;
        }
    }

    public DeliveryMethodType integerToDeliveryMethod(final Integer method) {
        switch (method) {
            case -1:
                return NotSet;
            case 0:
                return Pull;
            case 1:
                return Push;
            default:
                return null;
        }
    }

    public DeliveryProtocolType integerToDeliveryProtocol(final Integer protocol) {
        switch (protocol) {
            case -1:
                return DeliveryProtocolType.NotSet;
            case 0:
                return CoAP;
            case 1:
                return CoAPS;
            case 2:
                return HTTP;
            case 3:
                return HTTPS;
            case 4:
                return CoAPoverTCP;
            case 5:
                return CoAPoverTLS;
            default:
                return null;
        }
    }

    public Integer deliveryProtocolToInteger(final DeliveryProtocolType protocol) {
        switch (protocol) {
            case NotSet:
                return -1;
            case CoAP:
                return 0;
            case CoAPS:
                return 1;
            case HTTP:
                return 2;
            case HTTPS:
                return 3;
            case CoAPoverTCP:
                return 4;
            case CoAPoverTLS:
                return 5;
            default:
                return null;
        }
    }

    private static String getReplacedString(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "." + "i" + ".");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public List<ProvisionParameter> provisionEntitiesToProvisionParameters(
            final List<DeviceProvisionEntity> entities,
            final ClientType clientType,
            final String zoneId,
            final String dateFormat,
            final String timeFormat) {
        return entities.stream()
                .filter(Objects::nonNull)
                .map(e -> provisionEntityToProvisionParameter(e, clientType, zoneId, dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    public ProvisionParameter provisionEntityToProvisionParameter(final DeviceProvisionEntity entity,
                                                                  final ClientType clientType, final String zoneId,
                                                                  final String dateFormat, final String timeFormat) {
        return ProvisionParameter.builder()
                .id(entity.getId())
                .type(PARAMETERS)
                .priority(entity.getPriority())
                .user(StringUtils.substringAfter(entity.getUpdater(), "/"))
                .application(StringUtils.substringBefore(entity.getUpdater(), "/"))
                .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                .updated(DateTimeUtils.formatAcs(entity.getUpdated(), clientType, zoneId, dateFormat, timeFormat))
                .parameterName(parameterNameService.getNameById(entity.getNameId()))
                .value(entity.getValue())
                .build();
    }



    public List<ProvisionRpc> provisionEntitiesToProvisionRpcs(final List<DeviceRpcEntity> entities,
                                                               final ClientType clientType, final String dateFormat,
                                                               final String timeFormat, final String zoneId) {
        return entities.stream()
                .filter(Objects::nonNull)
                .map(e -> provisionEntityToProvisionRpc(e, clientType, dateFormat, timeFormat, zoneId))
                .collect(Collectors.toList());
    }

    public TaskParam activityDetailsToTaskParam(DeviceActivityDetailsEntity entity) {
        return TaskParam.builder()
                .name(entity.getName())
                .value(entity.getValue())
                .creator(entity.getCreator())
                .build();
    }

    public ProvisionRpc provisionEntityToProvisionRpc(final DeviceRpcEntity entity, final ClientType clientType,
                                                      final String dateFormat, final String timeFormat,
                                                      final String zoneId) {
        return ProvisionRpc.builder()
                .id(entity.getId())
                .type(RPC)
                .priority(entity.getPriority())
                .user(StringUtils.substringAfter(entity.getUpdater(), "/"))
                .application(StringUtils.substringBefore(entity.getUpdater(), "/"))
                .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                .updated(DateTimeUtils.formatAcs(entity.getUpdated(), clientType, null, dateFormat, timeFormat))
                .request(entity.getRequest())
                .build();
    }

    public List<ProvisionObject> provisionEntitiesToProvisionObjects(final List<DeviceProvisionObjectEntity> entities,
                                                                     final ClientType clientType, final String dateFormat,
                                                                     final String timeFormat, final String zoneId) {
        return entities.stream()
                .filter(Objects::nonNull)
                .map(e -> provisionEntityToProvisionObject(e, clientType, dateFormat, timeFormat, zoneId))
                .collect(Collectors.toList());
    }

    public ProvisionObject provisionEntityToProvisionObject(final DeviceProvisionObjectEntity entity,
                                                            final ClientType clientType, final String dateFormat,
                                                            final String timeFormat, final String zoneId) {
        return ProvisionObject.builder()
                .id(entity.getId())
                .type(OBJECTS)
                .priority(entity.getPriority())
                .user(StringUtils.substringAfter(entity.getUpdater(), "/"))
                .application(StringUtils.substringBefore(entity.getUpdater(), "/"))
                .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                .updated(DateTimeUtils.formatAcs(entity.getUpdated(), clientType, null, dateFormat, timeFormat))
                .path(parameterNameService.getNameById(entity.getNameId()))
                .name(getShortParameterName(parameterNameService.getNameById(entity.getNameId())))
                .build();
    }

    private String getShortParameterName(String fullName) {
        if (fullName.contains(".")) {
            int lastIndex = StringUtils.lastIndexOf(fullName, ".");
            if (lastIndex == fullName.length() - 1) {
                String name = fullName.substring(0, lastIndex - 1);
                if (name.contains(".")) {
                    int firstIndex = StringUtils.lastIndexOf(name, ".");
                    return fullName.substring(firstIndex + 1, lastIndex);
                } else {
                    return name;
                }
            }
            return fullName.substring(lastIndex + 1);
        }
        return fullName;
    }

    public List<ProvisionDownload> provisionEntitiesToProvisionDownloads(final List<DeviceProvisionFileEntity> entities,
                                                                         final ClientType clientType,
                                                                         final String dateFormat, final String timeFormat,
                                                                         String httpServer, final String zoneId) {
        return entities.stream()
                .filter(Objects::nonNull)
                .map(e -> provisionEntityToProvisionDownload(e, clientType, dateFormat, timeFormat, httpServer, zoneId))
                .collect(Collectors.toList());
    }

    public ProvisionDownload provisionEntityToProvisionDownload(final DeviceProvisionFileEntity entity,
                                                                final ClientType clientType, final String dateFormat,
                                                                final String timeFormat, String httpServer,
                                                                final String zoneId) {
        String link = entity.getUrl();
        String url = StringUtils.isNotBlank(link) ? link.substring(0, link.lastIndexOf("/") + 1) : "";
        String fileName = StringUtils.isNotBlank(link) ? link.substring(link.lastIndexOf("/") + 1) : "";
        return ProvisionDownload.builder()
                .id(entity.getId())
                .type(DOWNLOAD)
                .priority(entity.getPriority())
                .user(StringUtils.substringAfter(entity.getUpdater(), "/"))
                .application(StringUtils.substringBefore(entity.getUpdater(), "/"))
                .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                .updated(DateTimeUtils.formatAcs(entity.getUpdated(), clientType, null, dateFormat, timeFormat))
                .description(entity.getDescription())
                .url(url)
                .link(link)
                .fileName(fileName)
                .isManual(!url.contains(httpServer))
                .fileType(entity.getFileType())
                .build();
    }

    public List<CustomRpc> customRpcEntitiesToCustomRpcs(final List<Map<String, Object>> entities,
                                                         final ClientType clientType,
                                                         final String dateFormat,
                                                         final String timeFormat) {
        return entities.stream()
                .filter(Objects::nonNull)
                .map(e -> customRpcEntityToCustomRpc(e, clientType, dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    public CustomRpc customRpcEntityToCustomRpc(final Map<String, Object> entity,
                                                final ClientType clientType,
                                                final String dateFormat,
                                                final String timeFormat) {
        final Object completedObj = entity.get("completed");
        final Instant created = ((Timestamp) entity.get("created")).toInstant();
        final Instant completed = completedObj != null ? ((Timestamp) completedObj).toInstant() : null;

        return CustomRpc.builder()
                .taskId(((BigInteger) entity.get("id")).longValue())
                .state((String) entity.get("state"))
                .application(StringUtils.substringBefore((String) entity.get("creator"), "/"))
                .creator(StringUtils.substringAfter((String) entity.get("creator"), "/"))
                .createdIso(created)
                .created(DateTimeUtils.formatAcs(created, clientType, null, dateFormat, timeFormat))
                .completedIso(completed)
                .completed(DateTimeUtils.formatAcs(completed, clientType, null, dateFormat, timeFormat))
                .method((String) entity.get("method"))
                .request((String) entity.get("request"))
                .response((String) entity.get("response"))
                .build();
    }

    public List<DeviceObjectSimple> paramsToTabTree(final List<String> dbParams, final Map<String, DeviceTabView> strings) {
        final Map<Boolean, List<String>> objectParamsMap =
                dbParams.stream()
                        .collect(Collectors.groupingBy(p -> p.endsWith(".")));
        final List<DeviceParameterSimple> params = objectParamsMap.getOrDefault(FALSE, new ArrayList<>())
                .stream()
                .map(o -> fullNameToParameter(o, strings))
                .collect(Collectors.toList());
        return buildObjectTree(objectParamsMap.getOrDefault(TRUE, new ArrayList<>())
                .stream()
                .map(this::fullNameToObject)
                .sorted(Comparator.comparing(DeviceObjectSimple::getShortName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList()), params);
    }

    private DeviceParameterSimple fullNameToParameter(final String name, final Map<String, DeviceTabView> tabViewMap) {
        int index = name.lastIndexOf(".");
        final String fullName;
        final String shortName;
        final String parentName;

        if (index == name.length() - 1) {
            fullName = name.substring(0, index);
            index = fullName.lastIndexOf(".");
        } else {
            fullName = name;
        }

        if (StringUtils.countMatches(fullName, ".") == 1
                && index == fullName.length() - 1) {
            shortName = fullName.substring(0, index);
            parentName = null;
        } else if (index != -1) {
            parentName = fullName.substring(0, index);
            if (index == fullName.length() - 1) {
                shortName = fullName.substring(0, index);
            } else {
                shortName = fullName.substring(index + 1);
            }
        } else {
            shortName = fullName;
            parentName = null;
        }
        DeviceTabView tabView = tabViewMap.get(fullName.replaceAll("\\.[0-9]\\.", ".i."));

        TabViewType type = tabView == null ? null : tabView.getType() == null ? TabViewType.textbox : TabViewType.valueOf(tabView.getType());
        if (fullName.indexOf("Password") > 0 ||
                fullName.indexOf(".WEPKey") > 0 ||
                fullName.indexOf(".PreSharedKey") > 0 ||
                fullName.indexOf(".KeyPassphrase") > 0) {
            type = TabViewType.password;
        }
        return DeviceParameterSimple.builder()
                .fullName(fullName)
                .shortName(shortName)
                .parentName(parentName)
                .possibleValues(tabView == null ? null : tabView.getValues())
                .valueType(type)
                .build();
    }

    private DeviceObjectSimple fullNameToObject(final String name) {
        return DeviceObjectSimple.builder()
                .fullName(name)
                .shortName(getShortName(name))
                .parentName(getParentName(name))
                .items(new ArrayList<>())
                .parameters(new ArrayList<>())
                .build();
    }

    private String getParentName(final String fullName) {
        final int index = fullName.lastIndexOf(".");
        return index != -1 && index != 0 ? fullName.substring(0, index) : null;
    }

    private String getShortName(final String fullName) {
        final String str = StringUtils.chop(fullName);
        final int index = str.lastIndexOf(".");
        if (index != -1) {
            if (index == fullName.length() - 1) {
                return fullName.substring(0, index + 1);
            } else {
                final String temp = fullName.substring(index + 1);
                return NumberUtils.isCreatable(temp) ? getShortName(fullName.substring(0, index)) + "." + temp : temp;
            }
        } else {
            return fullName;
        }
    }

    private List<DeviceObjectSimple> buildObjectTree(final List<DeviceObjectSimple> objects,
                                                     final List<DeviceParameterSimple> params) {
        if (objects.isEmpty() && !params.isEmpty()) {
            Map<String, List<DeviceParameterSimple>> collect = params.stream()
                    .collect(Collectors.groupingBy(DeviceParameterSimple::getParentName));


            return collect.entrySet().stream()
                    .map(entry -> new DeviceObjectSimple().toBuilder()
                            .shortName(getShortName(entry.getKey()) + ".")
                            .fullName(entry.getKey() + ".")
                            .parameters(new ArrayList<>(entry.getValue())
                                    .stream()
                                    .filter(p -> !p.getShortName().toLowerCase().contains("battery"))
                                    .collect(Collectors.toList()))
//                                    .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(DeviceParameterSimple::getShortName, String.CASE_INSENSITIVE_ORDER)))))
                            .build())
                    .sorted(Comparator.comparing(DeviceObjectSimple::getShortName, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
        }

        final Map<String, DeviceObjectSimple> values =
                objects.stream()
                        .collect(Collectors.toMap(DeviceObjectSimple::getFullName, o -> o,
                                (u, v) -> {
                                    throw new IllegalStateException(
                                            String.format("Duplicate key %s", u));
                                }, LinkedHashMap::new));
        final List<String> objToRemove = new ArrayList<>();

        values.values()
                .forEach(entity -> {
                    final String parentName = entity.getParentName();
                    final boolean isContains = values.containsKey(parentName);
                    if (isContains) {
                        values.computeIfPresent(parentName, (key, value) -> {
                            value.getItems().add(entity);
                            objToRemove.add(entity.getFullName());
                            return value;
                        });
                    } else {
                        final String str = StringUtils.chop(parentName);
                        final int index = str.lastIndexOf(".");
                        if (index != -1) {
                            values.computeIfPresent(parentName.substring(0, index + 1), (key, value) -> {
                                value.getItems().add(entity);
                                objToRemove.add(entity.getFullName());
                                return value;
                            });
                        }
                    }
                    entity.getParameters()
                            .addAll(params.stream()
                                    .filter(p -> p.getParentName().equals(StringUtils.chop(entity.getFullName())))
                                    .sorted(Comparator.comparing(DeviceParameterSimple::getShortName, String.CASE_INSENSITIVE_ORDER))
                                    .collect(Collectors.toList()));
                });
        objToRemove.forEach(values::remove);
        List<DeviceObjectSimple> list = new ArrayList<>(values.values());

        return sortTree(list);

    }

    public List<DeviceObjectSimple> sortTree(List<DeviceObjectSimple> list) {

        list.forEach(device -> {
            if (!device.getItems().isEmpty()) {
                sortTree(new ArrayList<>(device.getItems()));
            }
            sortSubTree(device);
            sortSubParameters(device);
        });
        return list;
    }


    public void sortSubParameters(DeviceObjectSimple device) {

        device.setParameters(new ArrayList<>(device.getParameters()
                .stream()
                .sorted(Comparator.comparing(DeviceParameterSimple::getShortName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList())));
    }


    public void sortSubTree(DeviceObjectSimple device) {
        Comparator<DeviceObjectSimple> comparatorDeviceObjectSimple =
                Comparator.comparing((DeviceObjectSimple o) -> o.getShortName().replaceAll("\\d", ""), String.CASE_INSENSITIVE_ORDER)
                        .thenComparingInt(o -> {
                            String[] parts = o.getShortName().split("\\D+");
                            return parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                        });

        device.setItems(new ArrayList<>(device.getItems()
                .stream()
                .sorted(comparatorDeviceObjectSimple)
                .collect(Collectors.toList())));
    }

    public DeviceHistoryDetails entityToDeviceHistoryDetails(DeviceHistoryDetailsProjection details, ClientType clientType, String dateFormat,
                                                             String timeFormat, final String zoneId) {
        return DeviceHistoryDetails.builder()
                .oldValue(details.getPrevValue() == null ? "N/A" : details.getPrevValue())
                .value(details.getCurValue())
                .createdIso(DateTimeUtils.serverToUtc(details.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(details.getCreated(), clientType, null,
                        dateFormat, timeFormat))
                .parameterName(parameterNameService.getNameById(details.getNameId()))
                .build();

    }
}
