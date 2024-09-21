package com.friendly.services.device.info.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AccountInfo;
import com.friendly.commons.models.device.AccountInfoBody;
import com.friendly.commons.models.device.AccountInfoRequest;
import com.friendly.commons.models.device.AddDeviceRequest;
import com.friendly.commons.models.device.Device;
import com.friendly.commons.models.device.DeviceExtParamsBody;
import com.friendly.commons.models.device.DeviceInfo;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.response.DeviceInfoResponse;
import com.friendly.commons.models.device.response.DevicesResponse;
import com.friendly.commons.models.reports.DeviceActivityLog;
import com.friendly.commons.models.reports.DeviceReportXml;
import com.friendly.commons.models.reports.DeviceXml;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.commons.models.view.ConditionLogic;
import com.friendly.commons.models.view.ViewColumn;
import com.friendly.commons.models.view.ViewCondition;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.device.info.mapper.DeviceMapper;
import com.friendly.services.device.info.orm.acs.model.CpeEntity;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.device.info.orm.acs.model.DeviceInfoEntity;
import com.friendly.services.device.info.orm.acs.model.projections.CpeSerialProtocolIdGroupIdProjection;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.info.orm.acs.repository.CustomDeviceRepository;
import com.friendly.services.device.info.orm.acs.repository.DeviceInfoRepository;
import com.friendly.services.device.info.orm.acs.repository.DeviceRepository;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.device.info.utils.DeviceViewUtil;
import com.friendly.services.device.info.utils.helper.QueryViewHelper;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProtocolDetail;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.uiservices.view.ViewService;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnEntity;
import com.friendly.services.uiservices.view.orm.iotw.repository.ColumnRepository;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import com.ftacs.Cpe;
import com.ftacs.CpeList;
import com.ftacs.CustDeviceWS;
import com.ftacs.Exception_Exception;
import com.ftacs.IntegerArrayWS;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.DeviceActivityType.DELETE_DEVICE;
import static com.friendly.commons.models.reports.ReportType.DEVICE_UPDATE;
import static com.friendly.commons.models.view.ConditionType.IsNull;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DENIED_DOMAIN;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DEVICE_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.REPORT_IS_EMPTY;

/**
 * Service that exposes the base functionality for interacting with {@link Device} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
    @NonNull
    private final DeviceMapper deviceMapper;

    @NonNull
    private final ViewService viewService;

    @NonNull
    private final UserService userService;

    @NonNull
    private final DomainService domainService;

    @NonNull
    private final StatisticService statisticService;

    @NonNull
    private final ProductClassGroupRepository productClassGroupRepository;

    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final CustomDeviceRepository customDeviceRepository;

    @NonNull
    private final DeviceRepository deviceRepository;

    @NonNull
    private final DeviceInfoRepository deviceInfoRepository;

    @NonNull
    private final ParameterService parameterService;
    @NonNull
    private final ColumnRepository columnRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final ReportFileService reportFileService;

    @NonNull
    private final WsSender wsSender;

    @NonNull
    private final TemplateService templateService;

    private final DeviceUtils deviceUtils;


    public void createDevice(final String token, final AddDeviceRequest device) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final Integer domainId =
                user.getDomainId() == null || user.getDomainId() == -1 ? 0 : user.getDomainId();
        final String creator = clientType + "/" + user.getUsername();
        final CpeList cpeList = new CpeList();
        final Cpe cpe = new Cpe();
        cpe.setManufacturer(device.getManufacturer());
        cpe.setModel(device.getModel());
        cpe.setOui(device.getOui());
        cpe.setCpeProtocol(
                String.valueOf(DeviceUtils.convertProtocolTypeToId(device.getProtocolType())));
        cpe.setSerialNumber(device.getSerial());
        cpeList.getCpe().add(cpe);

        try {
            AcsProvider.getAcsWebService(clientType).addCPE(cpeList, domainId, creator);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    @Transactional
    public void deleteDevice(final String token, final List<Long> deviceIds) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        final IntegerArrayWS ids = new IntegerArrayWS();
        ids.getId().addAll(deviceIds.stream().map(Math::toIntExact).collect(Collectors.toList()));
        try {
            final List<DeviceEntity> devices = deviceRepository.findAllById(deviceIds);
            devices.forEach(
                    device -> {
                        Long deviceId = device.getId();
                        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
                        final Long groupId = cpeProjection.getGroupId();
                        statisticService.addDeviceLogAct(
                                DeviceActivityLog.builder()
                                        .userId(session.getUserId())
                                        .clientType(clientType)
                                        .activityType(DELETE_DEVICE)
                                        .deviceId(deviceId)
                                        .groupId(groupId)
                                        .serial(device.getSerial())
                                        .build());
                    });

            AcsProvider.getAcsWebService(clientType).deleteCPE(ids, false, true);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public DevicesResponse getDeviceByAccountInfo(String token, AccountInfoRequest request) {
        final Session session = jwtService.getSession(token);
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final List<Integer> domainIds =
                domainService
                        .getDomainIdByUserId(userId)
                        .map(domainService::getChildDomainIds)
                        .orElse(null);

        Specification<DeviceEntity> searchFilters = QueryViewHelper.getSearchFilters(domainIds, request);
        List<DeviceEntity> entities = deviceRepository.findAll(searchFilters);

        return new DevicesResponse(extendDeviceEntityWithParameterValues(entities, session, user));
    }

    public List<Device> extendDeviceEntityWithParameterValues(
            List<DeviceEntity> entities, Session session, UserResponse user) {
        List<Long> deviceIds = entities.stream().map(DeviceEntity::getId).collect(Collectors.toList());

        Map<Long, Map<String, String>> parametersByCpeIds =
                parameterService.findDevicesPropertiesNameValuesMap(deviceIds);

        return entities.stream()
                .map(d -> deviceMapper.deviceEntityToDevice(session, user, d, parametersByCpeIds, session.getClientType()))
                .collect(Collectors.toList());
    }

    public FTPage<Device> getDevicesByList(final String token, final DeviceExtParamsBody params) {
        final Session session = jwtService.getSession(token);
        viewService.validateView(params.getViewId(), DeviceViewUtil.getViewType(params.getDisplayType()));
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final List<Integer> domainIds = user.getDomainId() == null || user.getDomainId() == 0 ? null :
                domainService
                        .getDomainIdByUserId(userId)
                        .map(domainService::getChildDomainIds)
                        .orElse(null);
        final List<ColumnEntity> columns = columnRepository.findAllByViewId(params.getViewId());
        List<Page<DeviceEntity>> deviceEntityPage;
        if (params.getConditions() != null) {
            params.getConditions().stream()
                    .filter(condition -> IsNull.equals(condition.getCompare()))
                    .forEach(condition -> condition.setConditionString(null));
        }
//      switch (params.getDisplayType()) {
//      case LIST:
        deviceEntityPage = getDeviceByList(session, params, domainIds, columns);
//        break;
//      case SEARCH:
//        deviceEntityPage = getDevicesBySearch(params, domainIds, columns);
//        break;
//      default:
//        throw new FriendlyIllegalArgumentException(NOT_SUPPORTED_TYPE, params.getDisplayType());
//    }
        return getDevicePage(session, user, deviceEntityPage);
    }



    private List<Page<DeviceEntity>> getDeviceByList(
            final Session session,
            final DeviceExtParamsBody params,
            final List<Integer> domainIds,
            final List<ColumnEntity> columns) {
        final List<ViewCondition> conditions = params.getConditions();
        final ProtocolType protocolType = params.getProtocolType();
        final Long exceptDeviceId = params.getExceptDeviceId();
        final Long viewId = params.getViewId();
        String zoneId = session.getZoneId();
        ClientType clientType = session.getClientType();
        Specification<DeviceEntity> filters = conditions == null
                ? getListFilters(viewId, domainIds, null, null, protocolType, exceptDeviceId,
                clientType, zoneId)
                : getListFilters(viewId, domainIds, null, null, protocolType, exceptDeviceId,
                clientType, conditions, zoneId);

        return PageUtils.createPageRequest(
                        params.getPageNumbers(), params.getPageSize(), params.getSorts(), columns)
                .stream()
                .map(p -> deviceRepository.findAll(filters, p))
                .collect(Collectors.toList());
    }

    private List<Page<DeviceEntity>> getDevicesBySearch(
            final DeviceExtParamsBody params,
            final List<Integer> domainIds,
            final List<ColumnEntity> columns) {
        return PageUtils.createPageRequest(
                        params.getPageNumbers(), params.getPageSize(), params.getSorts(), columns)
                .stream()
                .map(
                        p ->
                                getDeviceEntities(
                                        params.getSearchColumn(),
                                        params.getSearchParam(),
                                        params.getSearchExact(),
                                        params.getProtocolType(),
                                        params.getExceptDeviceId(),
                                        domainIds,
                                        p))
                .collect(Collectors.toList());
    }

    @Async
    public void generateDeviceListXml(final Session session, final Map<String, Object> params) {
        final Integer viewIdInt = (Integer) params.get("viewId");
        final Long viewId = viewIdInt == null ? null : Long.valueOf(viewIdInt);
        final String manufacturer = (String) params.get("manufacturer");
        final String model = (String) params.get("model");
        final ProtocolType protocolType = (ProtocolType) params.get("protocolType");
        final Integer exceptDeviceIdInt = (Integer) params.get("exceptDeviceId");
        final Long exceptDeviceId = exceptDeviceIdInt == null ? null : Long.valueOf(exceptDeviceIdInt);
        final Long userId = session.getUserId();
        final Optional<Integer> userDomainId = domainService.getDomainIdByUserId(userId);
        final List<Integer> domainIds = userDomainId.map(domainService::getChildDomainIds).orElse(null);

        String zoneId = session.getZoneId();
        final List<DeviceXml> devices =
                deviceRepository
                        .findAll(
                                getListFilters(
                                        viewId,
                                        domainIds,
                                        manufacturer,
                                        model,
                                        protocolType,
                                        exceptDeviceId,
                                        session.getClientType(),
                                        zoneId))
                        .stream()
                        .map(deviceMapper::deviceEntityToDeviceXml)
                        .collect(Collectors.toList());

        if (devices.isEmpty()) {
            throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
        }

        final DeviceReportXml deviceReport = DeviceReportXml.builder().devices(devices).build();

        final String path =
                reportFileService.createXml(
                        userDomainId.orElse(null), DEVICE_UPDATE, deviceReport, DeviceReportXml.class);

        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Async
    public void generateDeviceListCsv(final Session session, final Map<String, Object> params) {
        final Integer viewIdInt = (Integer) params.get("viewId");
        final Long viewId = viewIdInt == null ? null : Long.valueOf(viewIdInt);
        final String manufacturer = (String) params.get("manufacturer");
        final String model = (String) params.get("model");
        final ProtocolType protocolType = (ProtocolType) params.get("protocolType");
        final Integer exceptDeviceIdInt = (Integer) params.get("exceptDeviceId");
        final Long exceptDeviceId = exceptDeviceIdInt == null ? null : Long.valueOf(exceptDeviceIdInt);
        final Long userId = session.getUserId();
        String zoneId = session.getZoneId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, zoneId);
        final Optional<Integer> userDomainId = domainService.getDomainIdByUserId(userId);
        final List<Integer> domainIds = userDomainId.map(domainService::getChildDomainIds).orElse(null);

        List<ViewColumn> columns = null;
        if (viewId != null) {
            columns =
                    viewService.getViewColumns(viewId, user.getLocaleId()).stream()
                            .filter(c -> c.getIndexVisible() != null)
                            .collect(Collectors.toList());
        }

        List<DeviceEntity> entities =
                deviceRepository.findAll(
                        getListFilters(
                                viewId,
                                domainIds,
                                manufacturer,
                                model,
                                protocolType,
                                exceptDeviceId,
                                session.getClientType(),
                                zoneId));
        List<Device> devices = extendDeviceEntityWithParameterValues(entities, session, user);

        if (devices.isEmpty()) {
            throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
        }

        final String path =
                ReportFileService.createCsv(userDomainId.orElse(null), DEVICE_UPDATE, devices, columns);

        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    public DeviceInfoResponse getDeviceInfo(final String token, final Long deviceId) {
        final Session session = jwtService.getSession(token);
        final UserResponse user =
                userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        Optional<DeviceInfo> deviceInfo =
                getDeviceInfo(deviceId, session, user);
        if (deviceUtils.validateDeviceDomain(token, deviceId) && deviceInfo.isPresent()) {
            return DeviceInfoResponse.fromDeviceInfo(deviceInfo.get());
        } else {
            throw new FriendlyEntityNotFoundException(DENIED_DOMAIN);
        }
    }



    public Optional<DeviceInfo> getDeviceInfo(
            final Long deviceId, final Session session, final UserResponse user) {
        Optional<DeviceInfoEntity> deviceInfoEntity = deviceInfoRepository.findById(deviceId);
        return deviceInfoEntity.map(
                device -> deviceMapper.deviceEntityToDeviceInfo(device, session, user));
    }

    public AccountInfo getAccountInfo(final  String token, final Long deviceId) {
        jwtService.getSession(token);

        return getAccountInfo(deviceId);
    }

    private AccountInfo getAccountInfo(final Long deviceId) {
        final Optional<CpeEntity> deviceEntity = cpeRepository.findById(deviceId);
        if (!deviceEntity.isPresent()) {
            throw new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND, deviceId);
        }

        return deviceEntity
                .map(CpeEntity::getSerial)
                .map(customDeviceRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(c -> deviceMapper.customDeviceToSubscriberInfo(c, deviceEntity.get()))
                .orElse(
                        AccountInfo.createEmpty(
                                deviceEntity.get().getDomainId(), deviceEntity.get().getDomainName()));
    }


    @Transactional
    public AccountInfo updateAccountInfo(
            final String token, /*final Long deviceId, final AccountInfo info*/
            AccountInfoBody accountInfo) {
        final ClientType clientType = jwtService.getClientTypeByHeaderAuth(token);

        final Optional<CpeEntity> cpeEntity = cpeRepository.findById(accountInfo.getDeviceId());
        if (!cpeEntity.isPresent()) {
            throw new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND, accountInfo.getDeviceId());
        }
        // TODO: change to update by ACS
        cpeRepository.saveAndFlush(
                cpeEntity.get().toBuilder().domainId(accountInfo.getAccountInfo().getDomainId()).build());

        final Optional<CustDeviceWS> custDeviceWS =
                cpeEntity
                        .map(CpeEntity::getSerial)
                        .map(s -> deviceMapper.accountInfoToCustDeviceWS(s, accountInfo.getAccountInfo()));
        if (custDeviceWS.isPresent()) {
            try {
                AcsProvider.getAcsWebService(clientType).updateAccountInfo(custDeviceWS.get());
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        }
        return getAccountInfo(accountInfo.getDeviceId());
    }

    private Page<DeviceEntity> getDeviceEntities(
            final String searchColumn,
            final String searchParam,
            final Boolean searchExact,
            final ProtocolType protocolType,
            final Long exceptDeviceId,
            final List<Integer> domainIds,
            final Pageable p) {
        final Integer protocolId =
                protocolType == null ? null : DeviceUtils.convertProtocolTypeToId(protocolType);

        return deviceRepository.findAll(
                QueryViewHelper.getSearchFilters(
                        domainIds, protocolId, exceptDeviceId, searchColumn, searchParam, searchExact),
                p);
    }

    private FTPage<Device> getDevicePage(
            final Session session,
            final UserResponse user,
            final List<Page<DeviceEntity>> deviceUpdateEntityPage) {
        List<DeviceEntity> deviceEntity =
                deviceUpdateEntityPage.stream()
                        .flatMap(p -> p.getContent().stream())
                        .collect(Collectors.toList());

        final List<Device> deviceList =
                extendDeviceEntityWithParameterValues(deviceEntity, session, user);
        final FTPage<Device> deviceUpdatePage = new FTPage<>();
        return deviceUpdatePage.toBuilder()
                .items(deviceList)
                .pageDetails(PageUtils.buildPageDetails(deviceUpdateEntityPage))
                .build();
    }

    public Specification<DeviceEntity> getListFilters(final Long viewId, final List<Integer> domainIds,
                                                      final String manufacturer, final String model,
                                                      final ProtocolType protocolType, final Long exceptDeviceId,
                                                      final ClientType clientType, final String zoneId) {
        return (root, cq, cb) -> {
            final Predicate mainPredicate =
                    QueryViewHelper.getPredicate(
                            domainIds, manufacturer, model, protocolType, exceptDeviceId, root, cb);
            return viewService.getFilter(
                    root, cb, viewId, ConditionLogic.And, mainPredicate, null, clientType, cq, zoneId);
        };
    }

    public Specification<DeviceEntity> getListFilters(
            final Long viewId,
            final List<Integer> domainIds,
            final String manufacturer,
            final String model,
            final ProtocolType protocolType,
            final Long exceptDeviceId,
            final ClientType clientType,
            final List<ViewCondition> conditions,
            final String zoneId) {
        return (root, cq, cb) -> {
            final Predicate mainPredicate =
                    QueryViewHelper.getPredicate(
                            domainIds, manufacturer, model, protocolType, exceptDeviceId, root, cb);
            return viewService.getFilter(
                    root, cb, viewId, ConditionLogic.And, mainPredicate, clientType, conditions, cq, zoneId);
        };
    }


    public Long getDeviceIdBySerial(final String serial) {
        return deviceRepository.findFirstBySerial(serial).map(DeviceEntity::getId).orElse(null);
    }

    public DeviceProtocolDetail getProtocolDetails(
            final String manufacturer, final String model, final String token) {
        jwtService.getSession(token);
        Optional<ProductClassGroupEntity> opt =
                productClassGroupRepository.findFirstByManufacturerNameAndModelOrderById(
                        manufacturer, model);
        if (!opt.isPresent()) {
            log.error("Manufacturer {} and model {} don't exist", manufacturer, model);
            throw new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND, manufacturer, model);
        }
        ProductClassGroupEntity productClassGroup = opt.get();
        final Long groupId = productClassGroup.getId();
        final ProtocolType protocolType =
                ProtocolType.fromValue(
                        productClassGroup.getProtocolId() == null ? 0 : productClassGroup.getProtocolId());
        final String protocolVersion;
        if (protocolType == ProtocolType.TR069) {
            String root = templateService.getRootParamNameFromTemplate(groupId);
            protocolVersion = root.equals("InternetGatewayDevice.") ? "TR098" : "TR181";
        } else {
            protocolVersion = null;
        }
        return DeviceProtocolDetail.builder()
                .protocolType(protocolType)
                .protocolVersion(protocolVersion)
                .build();
    }
}
