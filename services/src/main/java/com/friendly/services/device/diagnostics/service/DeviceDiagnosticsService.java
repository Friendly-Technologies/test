package com.friendly.services.device.diagnostics.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.services.management.action.dto.request.inheritors.DiagnosticTaskAction;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AddDeviceDiagnosticBody;
import com.friendly.commons.models.device.DeleteDeviceDiagnosticsBody;
import com.friendly.commons.models.device.DeviceDiagnosticsBody;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.TaskStateType;
import com.friendly.commons.models.device.diagnostics.AbstractDiagnosticRequest;
import com.friendly.commons.models.device.diagnostics.DeviceDiagnostics;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetail;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.commons.models.device.diagnostics.DiagnosticsTypeFilter;
import com.friendly.commons.models.device.diagnostics.DownloadDiagnosticRequest;
import com.friendly.commons.models.device.diagnostics.IPPingDiagnosticRequest;
import com.friendly.commons.models.device.diagnostics.LoopbackDiagnosticRequest;
import com.friendly.commons.models.device.diagnostics.NSLookupDiagnosticRequest;
import com.friendly.commons.models.device.diagnostics.TraceRouteDiagnosticRequest;
import com.friendly.commons.models.device.diagnostics.UdpEchoDiagnosticRequest;
import com.friendly.commons.models.device.diagnostics.UploadDiagnosticRequest;
import com.friendly.commons.models.device.response.DiagnosticInterfacesResponse;
import com.friendly.commons.models.device.response.DiagnosticsTypeFiltersResponse;
import com.friendly.commons.models.device.response.TaskKeyResponse;
import com.friendly.commons.models.reports.DeviceActivityLog;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.device.activity.service.TaskService;
import com.friendly.services.device.diagnostics.details.DiagnosticDetailsHandler;
import com.friendly.services.device.diagnostics.details.DiagnosticDetailsHandlerFactory;
import com.friendly.services.device.diagnostics.orm.acs.model.DeviceDiagnosticsEntity;
import com.friendly.services.device.diagnostics.orm.acs.repository.DeviceDiagnosticsRepository;
import com.friendly.services.device.diagnostics.root.DiagnosticRootStrategy;
import com.friendly.services.device.diagnostics.root.DiagnosticRootStrategyFactory;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;
import com.friendly.services.device.diagnostics.ws.DSLDiagnosticHandler;
import com.friendly.services.device.diagnostics.ws.DiagnosticToDiagnosticWsHandler;
import com.friendly.services.device.diagnostics.ws.DownloadDiagnosticHandler;
import com.friendly.services.device.diagnostics.ws.IPPingDiagnosticHandler;
import com.friendly.services.device.diagnostics.ws.LoopbackDiagnosticHandler;
import com.friendly.services.device.diagnostics.ws.NSLookupDiagnosticHandler;
import com.friendly.services.device.diagnostics.ws.NeighboringWifiDiagnosticHandler;
import com.friendly.services.device.diagnostics.ws.TraceRouteDiagnosticHandler;
import com.friendly.services.device.diagnostics.ws.UdpEchoDiagnosticHandler;
import com.friendly.services.device.diagnostics.ws.UploadDiagnosticHandler;
import com.friendly.services.device.info.orm.acs.model.projections.CpeSerialProtocolIdGroupIdProjection;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.info.utils.DeviceActivityUtil;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.settings.userinterface.InterfaceService;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.ftacs.CpeDiagParameterListWS;
import com.ftacs.CpeDiagnosticWS;
import com.ftacs.Exception_Exception;
import com.ftacs.IntegerArrayWS;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.friendly.commons.models.device.FTTaskTypesEnum.GetParameterAttributesList;
import static com.friendly.commons.models.device.FTTaskTypesEnum.GetParameterNamesOnly;
import static com.friendly.commons.models.device.ProtocolType.TR069;
import static com.friendly.commons.models.device.ProtocolType.USP;
import static com.friendly.commons.models.device.TaskStateType.COMPLETED;
import static com.friendly.commons.models.device.TaskStateType.FAILED;
import static com.friendly.commons.models.device.TaskStateType.PENDING;
import static com.friendly.commons.models.device.TaskStateType.SENT;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.DOWNLOAD_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.DSL_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.IP_PING_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.LOOPBACK_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.NEIGHBORING_WI_FI_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.NS_LOOKUP_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.TRACE_ROUTE_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.UDP_ECHO_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.UPLOAD_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.fromPartialName;
import static com.friendly.commons.models.reports.DeviceActivityType.ADD_DIAGNOSTICS;
import static com.friendly.commons.models.reports.DeviceActivityType.DELETE_DIAGNOSTICS;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DIAGNOSTIC_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PRODUCT_CLASS_GROUP_NOT_FOUND;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.BOM_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.DIAGNOSTICS_STATE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.DIAGNOSTICS_TYPE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.EOM_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.ROM_TIME;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceDiagnosticsService {

    @NonNull
    private final UserService userService;

    @NonNull
    private final StatisticService statisticService;

    @NonNull
    private final TaskService taskService;

    @NonNull
    private final DeviceDiagnosticsRepository deviceDiagnosticsRepository;

    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final ParameterNameService parameterNameService;

    @NonNull
    private final ParameterService parameterService;

    @NonNull
    private final InterfaceService interfaceService;
    @NonNull
    private final ProductClassGroupRepository productClassGroupRepository;
    @NonNull
    private final TemplateService templateService;


    public FTPage<DeviceDiagnostics> getDeviceDiagnostics(final String token, final DeviceDiagnosticsBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "id");
        final Long deviceId = body.getDeviceId();

        final ZonedDateTime zonedDateTime = AcsProvider.getAcsWebService(session.getClientType())
                .getServerDate().getDate().toGregorianCalendar()
                .toZonedDateTime();
        final Instant acsInstant = zonedDateTime.toInstant().plusSeconds(zonedDateTime.getOffset().getTotalSeconds());

        final List<Page<DeviceDiagnosticsEntity>> diagnosticPage =
                pageable.stream()
                        .map(p -> deviceDiagnosticsRepository.findAllByDeviceId(deviceId, p))
                        .collect(Collectors.toList());
        final int protocolTypeId = cpeRepository.getProtocolTypeByDevice(deviceId).orElse(100);
        ProtocolType protocolType = ProtocolType.fromValue(protocolTypeId);
        final List<DeviceDiagnostics> diagnostics =
                diagnosticPage.stream()
                        .map(Page::getContent)
                        .flatMap(d -> diagnosticsEntityToDiagnostics(d, protocolType, acsInstant,
                                        session.getClientType(),
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList());

        final FTPage<DeviceDiagnostics> page = new FTPage<>();
        return page.toBuilder()
                .items(diagnostics)
                .pageDetails(PageUtils.buildPageDetails(diagnosticPage))
                .build();
    }

    public DiagnosticDetails getDiagnosticDetails(final String token, final Long id) {
        final Session session = jwtService.getSession(token);
        final DeviceDiagnosticsEntity deviceDiagnosticsEntity = deviceDiagnosticsRepository.findById(id)
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(DIAGNOSTIC_NOT_FOUND, id));
        final DiagnosticType diagnosticType = fromPartialName(deviceDiagnosticsEntity.getDiagnosticsType());
        final List<DiagnosticDetail> details = new ArrayList<>();
        details.add(new DiagnosticDetail(DIAGNOSTICS_TYPE.getParamName(), diagnosticType.getDescription(),
                DIAGNOSTICS_TYPE.getName()));
        DiagnosticsDetailsUtil diagnosticsDetailsUtil = new DiagnosticsDetailsUtil(deviceDiagnosticsRepository);
        details.add(diagnosticsDetailsUtil.getDiagnosticParam(id, DIAGNOSTICS_STATE));

        DiagnosticDetailsHandler handler = DiagnosticDetailsHandlerFactory.getHandler(diagnosticType,
                diagnosticsDetailsUtil);
        DiagnosticDetails diagnosticDetails = handler.handleDiagnosticDetails(id, details);

        final TaskStateType diagnosticState = getTaskStateType(token, deviceDiagnosticsEntity);
        diagnosticDetails.setState(diagnosticState.getValue());

        if(diagnosticType == UPLOAD_DIAGNOSTIC) {
            for (DiagnosticDetail detail : diagnosticDetails.getDetails()) {
                if(detail.getValue() != null && (detail.getParameter().equals(BOM_TIME.getParamName())
                || detail.getParameter().equals(EOM_TIME.getParamName())
                        || detail.getParameter().equals(ROM_TIME.getParamName()))) {
                    Instant date = DiagnosticsDetailsUtil.getTimeValue(detail.getValue()).toInstant();
                    Instant convertedDate = DateTimeUtils.clientToServer(date, session.getClientType(),
                            session.getZoneId());
                    detail.setValue(convertedDate.toString());
                }
            }
        }

        return diagnosticDetails;
    }

    private TaskStateType getTaskStateType(String token, DeviceDiagnosticsEntity deviceDiagnosticsEntity) {
        final Session session = jwtService.getSession(token);
        final ZonedDateTime zonedDateTime = AcsProvider.getAcsWebService(session.getClientType())
                .getServerDate().getDate().toGregorianCalendar()
                .toZonedDateTime();
        final Instant acsInstant = zonedDateTime.toInstant().plusSeconds(zonedDateTime.getOffset().getTotalSeconds());
        final Long deviceId = deviceDiagnosticsEntity.getDeviceId();
        final int protocolTypeId = cpeRepository.getProtocolTypeByDevice(deviceId).orElse(100);
        ProtocolType protocolType = ProtocolType.fromValue(protocolTypeId);
        return getDiagnosticState(deviceDiagnosticsEntity, protocolType, acsInstant);
    }

    public DiagnosticsTypeFiltersResponse getDiagnosticTypes(final String token, final Long deviceId) {
        jwtService.getSession(token);
        final int protocolTypeId = cpeRepository.getProtocolTypeByDevice(deviceId).orElse(100);
        final ProtocolType protocolType = ProtocolType.fromValue(protocolTypeId);
        final List<DiagnosticsTypeFilter> filters = new ArrayList<>();
        for (DiagnosticType diagnosticType : DiagnosticType.values()) {
            String diagRoot = getDiagnosticRoot(protocolType, diagnosticType, deviceId);
            if (diagRoot != null) {
                DiagnosticsTypeFilter typeFilter = new DiagnosticsTypeFilter();
                typeFilter.setKey(diagnosticType);
                typeFilter.setName(diagnosticType.getDescription());
                filters.add(typeFilter);
            }
        }

        return new DiagnosticsTypeFiltersResponse(filters);
    }

    public DiagnosticsTypeFiltersResponse getDiagnosticTypesByProductClassGroup(final String token, final String manufacturer, final String model) {
        jwtService.getSession(token);
        ProductClassGroupEntity productClassGroup = productClassGroupRepository.findByManufacturerNameAndModel(manufacturer, model)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(PRODUCT_CLASS_GROUP_NOT_FOUND, manufacturer, model));
        final ProtocolType protocolType = ProtocolType.fromValue(productClassGroup.getProtocolId());
        final List<DiagnosticsTypeFilter> filters = new ArrayList<>();
        for (DiagnosticType diagnosticType : DiagnosticType.values()) {
            String diagRoot = getDiagnosticRootByGroupId(protocolType, diagnosticType, productClassGroup.getId());
            if (diagRoot != null) {
                DiagnosticsTypeFilter typeFilter = new DiagnosticsTypeFilter();
                typeFilter.setKey(diagnosticType);
                typeFilter.setName(diagnosticType.getDescription());
                filters.add(typeFilter);
            }
        }

        return new DiagnosticsTypeFiltersResponse(filters);
    }


    public DiagnosticInterfacesResponse getDiagnosticsInterfaces(final String token, final Long deviceId) {
        jwtService.getSession(token);

        List<String> interfaces = DeviceActivityUtil.getInterfaceParams()
                .stream()
                .flatMap(p -> getParams(deviceId, p, null).stream())
                .sorted()
                .collect(Collectors.toList());
        return new DiagnosticInterfacesResponse(interfaces);
    }

    public TaskKeyResponse addDiagnostic(final String token, final AddDeviceDiagnosticBody body) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final Long userId = session.getUserId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, session.getZoneId());
        final String username = clientType.name().toUpperCase() + "/" + user.getUsername();
        final AbstractDiagnosticRequest diagnosticRequest = body.getDiagnosticRequest();
        final Long deviceId = body.getDeviceId();
        final DiagnosticType diagnosticType = diagnosticRequest.getDiagnosticType();
        final IntegerArrayWS ids = new IntegerArrayWS();
        ids.getId().add(deviceId.intValue());
        final CpeDiagParameterListWS inputParams = new CpeDiagParameterListWS();
        final CpeDiagParameterListWS outputParams = new CpeDiagParameterListWS();
        final CpeDiagnosticWS cpeDiagnosticWS = new CpeDiagnosticWS();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final int protocolTypeId = Optional.ofNullable(cpeProjection.getProtocolId()).orElse(100);
        final String serial = cpeProjection.getSerial();

        final String root = getDiagnosticRoot(ProtocolType.fromValue(protocolTypeId), diagnosticRequest.getDiagnosticType(), deviceId);
        if (root == null) {
            log.error("Unable to identify diagnostics root from device: " + deviceId + " and diag: " + diagnosticRequest);
            return new TaskKeyResponse(null);
        }

        String defaultFileSize = UPLOAD_DIAGNOSTIC.equals(diagnosticType)
                ? interfaceService.getInterfaceValue(ClientType.mc, "UploadDiagnosticsFileSize").orElse("1000000")
                : "1000000";

        Map<DiagnosticType, Supplier<DiagnosticToDiagnosticWsHandler>> handlerMap = new EnumMap<>(DiagnosticType.class);
        handlerMap.put(TRACE_ROUTE_DIAGNOSTIC, () -> new TraceRouteDiagnosticHandler(deviceId,null, root, protocolTypeId,
                (TraceRouteDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS,
                parameterService, templateService));
        handlerMap.put(NS_LOOKUP_DIAGNOSTIC, () -> new NSLookupDiagnosticHandler(protocolTypeId, root,
                (NSLookupDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(DSL_DIAGNOSTIC, () -> new DSLDiagnosticHandler(protocolTypeId, root, inputParams,
                outputParams, cpeDiagnosticWS));
        handlerMap.put(IP_PING_DIAGNOSTIC, () -> new IPPingDiagnosticHandler(protocolTypeId, root,
                (IPPingDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(DOWNLOAD_DIAGNOSTIC, () -> new DownloadDiagnosticHandler(protocolTypeId, root,
                (DownloadDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(UPLOAD_DIAGNOSTIC, () -> new UploadDiagnosticHandler(protocolTypeId, root,
                (UploadDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS, serial,
                defaultFileSize, deviceId, parameterService));
        handlerMap.put(LOOPBACK_DIAGNOSTIC, () -> new LoopbackDiagnosticHandler(protocolTypeId, root,
                (LoopbackDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(NEIGHBORING_WI_FI_DIAGNOSTIC, () -> new NeighboringWifiDiagnosticHandler(protocolTypeId,
                root, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(UDP_ECHO_DIAGNOSTIC, () -> new UdpEchoDiagnosticHandler(protocolTypeId, root,
                (UdpEchoDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));

        Supplier<DiagnosticToDiagnosticWsHandler> handlerSupplier = handlerMap.get(diagnosticType);
        if (handlerSupplier != null) {
            DiagnosticToDiagnosticWsHandler handler = handlerSupplier.get();
            if (handler != null) {
                handler.handleDiagnosticToDiagnosticWs();
            }
        }

        outputParams.getCPEDiagParameter().addAll(inputParams.getCPEDiagParameter());
        cpeDiagnosticWS.setCpeDiagSetParameters(inputParams);
        cpeDiagnosticWS.setCpeDiagGetParameters(outputParams);

        try {
            final Long transactionId =
                    AcsProvider.getAcsWebService(clientType)
                            .addCPEDiag(ids, cpeDiagnosticWS, 3, diagnosticRequest.getPush(), username)
                            .getTransactionId();

            statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                    .userId(session.getUserId())
                    .clientType(session.getClientType())
                    .activityType(ADD_DIAGNOSTICS)
                    .serial(serial)
                    .groupId(cpeProjection.getGroupId())
                    .deviceId(deviceId)
                    .note(cpeDiagnosticWS.getCpeDiagName())
                    .build());

            return new TaskKeyResponse(taskService.getTaskKey(transactionId));
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public CpeDiagnosticWS addDiagnosticForUgTask(final DiagnosticTaskAction body, final Long groupId){
        final AbstractDiagnosticRequest diagnosticRequest = body.getDiagnosticRequest();
        final DiagnosticType diagnosticType = diagnosticRequest.getDiagnosticType();
        final CpeDiagParameterListWS inputParams = new CpeDiagParameterListWS();
        final CpeDiagParameterListWS outputParams = new CpeDiagParameterListWS();
        final CpeDiagnosticWS cpeDiagnosticWS = new CpeDiagnosticWS();
        final ProductClassGroupEntity productClassGroup = productClassGroupRepository.findById(groupId)
                .orElseThrow(() -> new FriendlyIllegalArgumentException(PRODUCT_CLASS_GROUP_NOT_FOUND));
        final int protocolTypeId = Optional.ofNullable(productClassGroup.getProtocolId()).orElse(100);

        final String root = getDiagnosticRootByGroupId(ProtocolType.fromValue(protocolTypeId), diagnosticRequest.getDiagnosticType(), groupId);
        if (root == null) {
            log.error("Unable to identify diagnostics root from product class group: " + groupId + " and diag: " + diagnosticRequest);
            return cpeDiagnosticWS;
        }

        String defaultFileSize = UPLOAD_DIAGNOSTIC.equals(diagnosticType)
                ? interfaceService.getInterfaceValue(ClientType.mc, "UploadDiagnosticsFileSize").orElse("1000000")
                : "1000000";

        Map<DiagnosticType, Supplier<DiagnosticToDiagnosticWsHandler>> handlerMap = new EnumMap<>(DiagnosticType.class);
        handlerMap.put(TRACE_ROUTE_DIAGNOSTIC, () -> new TraceRouteDiagnosticHandler(null,groupId, root, protocolTypeId,
                (TraceRouteDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS,
                parameterService,templateService));
        handlerMap.put(NS_LOOKUP_DIAGNOSTIC, () -> new NSLookupDiagnosticHandler(protocolTypeId, root,
                (NSLookupDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(DSL_DIAGNOSTIC, () -> new DSLDiagnosticHandler(protocolTypeId, root, inputParams,
                outputParams, cpeDiagnosticWS));
        handlerMap.put(IP_PING_DIAGNOSTIC, () -> new IPPingDiagnosticHandler(protocolTypeId, root,
                (IPPingDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(DOWNLOAD_DIAGNOSTIC, () -> new DownloadDiagnosticHandler(protocolTypeId, root,
                (DownloadDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(UPLOAD_DIAGNOSTIC, () -> new UploadDiagnosticHandler(protocolTypeId, root,
                (UploadDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS, null,
                defaultFileSize, null, parameterService));
        handlerMap.put(LOOPBACK_DIAGNOSTIC, () -> new LoopbackDiagnosticHandler(protocolTypeId, root,
                (LoopbackDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(NEIGHBORING_WI_FI_DIAGNOSTIC, () -> new NeighboringWifiDiagnosticHandler(protocolTypeId,
                root, inputParams, outputParams, cpeDiagnosticWS));
        handlerMap.put(UDP_ECHO_DIAGNOSTIC, () -> new UdpEchoDiagnosticHandler(protocolTypeId, root,
                (UdpEchoDiagnosticRequest) diagnosticRequest, inputParams, outputParams, cpeDiagnosticWS));

        Supplier<DiagnosticToDiagnosticWsHandler> handlerSupplier = handlerMap.get(diagnosticType);
        if (handlerSupplier != null) {
            DiagnosticToDiagnosticWsHandler handler = handlerSupplier.get();
            if (handler != null) {
                handler.handleDiagnosticToDiagnosticWs();
            }
        }

        outputParams.getCPEDiagParameter().addAll(inputParams.getCPEDiagParameter());
        cpeDiagnosticWS.setCpeDiagSetParameters(inputParams);
        cpeDiagnosticWS.setCpeDiagGetParameters(outputParams);
        return cpeDiagnosticWS;
    }

    @Transactional
    public void deleteDiagnostics(final String token, final DeleteDeviceDiagnosticsBody body) {
        final Session session = jwtService.getSession(token);
        final Long deviceId = body.getDeviceId();
        final List<Long> ids = body.getDiagnosticIds();
        CpeSerialProtocolIdGroupIdProjection cpeProjection =
                cpeRepository.findCpeSerialProtocolIdGroupIdProjectionByDeviceId(deviceId);
        final String serial = cpeProjection.getSerial();
        final Long groupId = cpeProjection.getGroupId();
        ids.forEach(id -> {
            final Optional<DeviceDiagnosticsEntity> diagnostics = deviceDiagnosticsRepository.findById(id);
            if (diagnostics.isPresent()) {
                taskService.deleteDiagnosticFromPendingTask(deviceId, id);
                deviceDiagnosticsRepository.deleteByIdAndDeviceId(id, deviceId);
                deviceDiagnosticsRepository.deleteGetDiagnostic(id);
                deviceDiagnosticsRepository.deleteSetDiagnostic(id);

                statisticService.addDeviceLogAct(DeviceActivityLog.builder()
                        .userId(session.getUserId())
                        .clientType(session.getClientType())
                        .activityType(DELETE_DIAGNOSTICS)
                        .deviceId(deviceId)
                        .groupId(groupId)
                        .serial(serial)
                        .build());
            }
        });
    }

    private List<String> getParams(final Long deviceId, final String param, final Long groupId) {
        final List<String> params = deviceId != null ? parameterService.getParamNamesLike(deviceId, param)
                : templateService.getParamNamesLike(groupId, param);
        final int occurrence = params.stream()
                .mapToInt(p -> StringUtils.countMatches(p, "."))
                .min()
                .orElse(0);
        return params.stream()
                .filter(p -> StringUtils.countMatches(p, ".") == occurrence)
                .collect(Collectors.toList());
    }



    public String getDiagnosticRoot(ProtocolType protocol, DiagnosticType diagType, final Long deviceId) {
        if (diagType == null || (isNotTR069(protocol) && isNotUsp(protocol))) {
            return null;
        }
        DiagnosticRootStrategy strategy = DiagnosticRootStrategyFactory.getStrategy(diagType);
        return strategy.getDiagnosticRoot(protocol, deviceId, parameterService);
    }

    public String getDiagnosticRootByGroupId(ProtocolType protocol, DiagnosticType diagType, final Long groupId) {
        if (diagType == null || (isNotTR069(protocol) && isNotUsp(protocol))) {
            return null;
        }
        DiagnosticRootStrategy strategy = DiagnosticRootStrategyFactory.getStrategy(diagType);
        return strategy.getDiagnosticRootByProductClass(protocol, groupId, templateService);
    }

    private static boolean isNotUsp(ProtocolType protocol) {
        return !USP.equals(protocol);
    }

    private static boolean isNotTR069(ProtocolType protocol) {
        return !TR069.equals(protocol);
    }

    public TaskStateType getDiagnosticState(DeviceDiagnosticsEntity deviceDiagnostics, ProtocolType protocolType,
                                            final Instant acsInstant) {
        TaskStateType taskState;
        boolean isOverdue = deviceDiagnostics.getCreated().plus(1, DAYS).isBefore(acsInstant);
        boolean isShortOverdue = deviceDiagnostics.getCreated().plusSeconds(120).isBefore(acsInstant);
        if (isOverdue) {
            taskState = deviceDiagnostics.getCompleted() == null ? FAILED : COMPLETED;
        } else {
            taskState = deviceDiagnostics.getRequestTaskState() == null ? null : TaskStateType.fromValue(deviceDiagnostics.getRequestTaskState());
            if (COMPLETED.equals(taskState)) {
                if (!USP.equals(protocolType)) { // 26091
                    taskState = deviceDiagnostics.getCompleteTaskState() == null ? null : TaskStateType.fromValue(deviceDiagnostics.getCompleteTaskState());
                }
                DiagnosticType diagnosticType = fromPartialName(deviceDiagnostics.getDiagnosticsType());
                if (COMPLETED.equals(taskState) && NEIGHBORING_WI_FI_DIAGNOSTIC.equals(diagnosticType)
                        && TR069.equals(protocolType)) {
                    Long deviceId = deviceDiagnostics.getDeviceId();
                    String root = getDiagnosticRoot(protocolType, diagnosticType, deviceId);
                    root = root.endsWith("Result.") ? root : root + "Result.";
                    Long nameId = parameterNameService.getIdByName(root);
                    if (nameId > 0) {
                        taskState = taskService.getTaskStateForParameter(deviceId, GetParameterAttributesList, nameId.intValue());
                        if (taskState == null) {
                            taskState = taskService.getTaskStateForParameter(deviceId, GetParameterNamesOnly, nameId.intValue());
                        }
                    }
                }
            }

            if (isShortOverdue) {
                if (taskState == null || PENDING.equals(taskState) || SENT.equals(taskState)) {
                    taskState = deviceDiagnostics.getCompleted() != null ? COMPLETED : FAILED;
                }
            } else {
                if (taskState == null) {
                    taskState = PENDING;
                }
                if (PENDING.equals(taskState) || SENT.equals(taskState)) {
                    return taskState;
                }
            }
        }

        String state = deviceDiagnostics.getState();
        if (state == null) {
            return taskState;
        }

        String lowerCaseState = state.toLowerCase();
        if (lowerCaseState.contains("complete")) {
            return COMPLETED;
        } else if (lowerCaseState.contains("fail") || lowerCaseState.contains("error")) {
            return FAILED;
        } else if (lowerCaseState.contains("request") && COMPLETED.equals(taskState)) {
            return isShortOverdue ? FAILED : PENDING;
        }

        return taskState;
    }

    public List<DeviceDiagnostics> diagnosticsEntityToDiagnostics(final List<DeviceDiagnosticsEntity> entities,
                                                                  ProtocolType protocolType, final Instant acsInstant,
                                                                  final ClientType clientType, final String zoneId,
                                                                  final String dateFormat, final String timeFormat) {

        return entities.stream()
                .map(d -> deviceDiagnosticEntityToDeviceDiagnostic(d, protocolType, acsInstant, clientType, zoneId,
                        dateFormat, timeFormat))
                .collect(Collectors.toList());
    }

    public DeviceDiagnostics deviceDiagnosticEntityToDeviceDiagnostic(final DeviceDiagnosticsEntity entity,
                                                                      final ProtocolType protocolType,
                                                                      final Instant acsInstant,
                                                                      final ClientType clientType, final String zoneId,
                                                                      final String dateFormat,
                                                                      final String timeFormat) {
        final DiagnosticType diagnosticsType = fromPartialName(entity.getDiagnosticsType());
        final Instant createdIso = DateTimeUtils.serverToClient(entity.getCreated(), clientType, zoneId);
        final TaskStateType stateType = getDiagnosticState(entity, protocolType, acsInstant);

        return DeviceDiagnostics.builder()
                .id(entity.getId())
                .state(stateType)
                .diagnosticsTypeKey(diagnosticsType.getName())
                .diagnosticsTypeName(diagnosticsType.getDescription())
                .createdIso(createdIso)
                .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId, dateFormat, timeFormat))
                .completedIso(entity.getCompleted() == null ? null
                        : DateTimeUtils.serverToClient(entity.getCompleted(), clientType, zoneId))
                .completed(entity.getCompleted() == null ? null
                        : DateTimeUtils.formatAcs(entity.getCompleted(), clientType, zoneId, dateFormat, timeFormat))
                .build();
    }

    public DiagnosticInterfacesResponse getDiagnosticsInterfacesByGroup(final String token, final String manufacturer, final String model) {
        jwtService.getSession(token);
        final Long groupId = productClassGroupRepository.getIdByManufacturerAndModel(manufacturer, model);
        List<String> interfaces = DeviceActivityUtil.getInterfaceParams()
                .stream()
                .flatMap(p -> getParams(null, p, groupId).stream())
                .sorted()
                .collect(Collectors.toList());
        return new DiagnosticInterfacesResponse(interfaces);
    }

}