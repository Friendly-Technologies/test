package com.friendly.services.device;

import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.device.AccountInfo;
import com.friendly.commons.models.device.AccountInfoBody;
import com.friendly.commons.models.device.AccountInfoRequest;
import com.friendly.commons.models.device.AddCustomRpcBody;
import com.friendly.commons.models.device.AddDeviceDiagnosticBody;
import com.friendly.commons.models.device.AddDeviceRequest;
import com.friendly.commons.models.device.AddFileBody;
import com.friendly.commons.models.device.AddMonitoringParamsBody;
import com.friendly.commons.models.device.AddObjectBody;
import com.friendly.commons.models.device.CurrentDeviceParametersBody;
import com.friendly.commons.models.device.CustomRpcBody;
import com.friendly.commons.models.device.DeleteCustomRpcBody;
import com.friendly.commons.models.device.DeleteDeviceDiagnosticsBody;
import com.friendly.commons.models.device.DeleteDeviceHistoryBody;
import com.friendly.commons.models.device.DeleteDeviceObjectsBody;
import com.friendly.commons.models.device.DeleteDeviceProvisionsBody;
import com.friendly.commons.models.device.DeleteFileBody;
import com.friendly.commons.models.device.DeleteParametersBody;
import com.friendly.commons.models.device.Device;
import com.friendly.commons.models.device.DeviceActivity;
import com.friendly.commons.models.device.DeviceActivityBody;
import com.friendly.commons.models.device.DeviceActivityTaskBody;
import com.friendly.commons.models.device.DeviceDiagnosticsBody;
import com.friendly.commons.models.device.DeviceExtParamsBody;
import com.friendly.commons.models.device.DeviceFilesBody;
import com.friendly.commons.models.device.DeviceHistory;
import com.friendly.commons.models.device.DeviceHistoryBody;
import com.friendly.commons.models.device.DeviceHistoryDetailsBody;
import com.friendly.commons.models.device.DeviceHistoryDetailsResponse;
import com.friendly.commons.models.device.DeviceLog;
import com.friendly.commons.models.device.DeviceParametersBody;
import com.friendly.commons.models.device.DeviceProvisionBody;
import com.friendly.commons.models.device.DeviceTraceBody;
import com.friendly.commons.models.device.EditDeviceProvisionBody;
import com.friendly.commons.models.device.FileDownloadNamesBody;
import com.friendly.commons.models.device.FileTypesFilterBody;
import com.friendly.commons.models.device.FileUploadInstancesBody;
import com.friendly.commons.models.device.GetNewParamsBody;
import com.friendly.commons.models.device.GetTargetFileNameBody;
import com.friendly.commons.models.device.InvokeMethodBody;
import com.friendly.commons.models.device.MacFilterBody;
import com.friendly.commons.models.device.MonitoringGraphBody;
import com.friendly.commons.models.device.ProtocolTypeRequest;
import com.friendly.commons.models.device.StartStopMonitoringGraphBody;
import com.friendly.commons.models.device.StartStopTraceBody;
import com.friendly.commons.models.device.TaskList;
import com.friendly.commons.models.device.TraceTxtBody;
import com.friendly.commons.models.device.UpdateDeviceParamsBody;
import com.friendly.commons.models.device.UpdateDeviceTemplateBody;
import com.friendly.commons.models.device.UserExperienceBody;
import com.friendly.commons.models.device.WifiAautoRescanBody;
import com.friendly.commons.models.device.diagnostics.DeviceDiagnostics;
import com.friendly.commons.models.device.diagnostics.DiagnosticDetails;
import com.friendly.commons.models.device.file.DeviceFile;
import com.friendly.commons.models.device.file.DownloadFileDetails;
import com.friendly.commons.models.device.frame.DeviceMacFilter;
import com.friendly.commons.models.device.frame.DeviceStatusDetails;
import com.friendly.commons.models.device.frame.DeviceStatusResponse;
import com.friendly.commons.models.device.frame.response.GetQoeDetailsResponse;
import com.friendly.commons.models.device.frame.response.GetSpeedTestResponse;
import com.friendly.commons.models.device.method.DeviceMethodSResponse;
import com.friendly.commons.models.device.monitoring.MonitoringGraph;
import com.friendly.commons.models.device.provision.AbstractProvision;
import com.friendly.commons.models.device.provision.DownloadProvisionDetails;
import com.friendly.commons.models.device.provision.DownloadProvisionDetailsBody;
import com.friendly.commons.models.device.request.DeviceConfigTypeRequest;
import com.friendly.commons.models.device.response.ActiveConnectionResponse;
import com.friendly.commons.models.device.response.AppPortsResponse;
import com.friendly.commons.models.device.response.AssociatedHosts;
import com.friendly.commons.models.device.response.DeviceActivityTaskNamesResponse;
import com.friendly.commons.models.device.response.DeviceConfigsResponse;
import com.friendly.commons.models.device.response.DeviceHistoryActivityTypesResponse;
import com.friendly.commons.models.device.response.DeviceInfoResponse;
import com.friendly.commons.models.device.response.DeviceObjectsResponse;
import com.friendly.commons.models.device.response.DeviceObjectsSimpleResponse;
import com.friendly.commons.models.device.response.DeviceServicesResponse;
import com.friendly.commons.models.device.response.DeviceSimplifiedParamsResponse;
import com.friendly.commons.models.device.response.DeviceTabsResponse;
import com.friendly.commons.models.device.response.DeviceTraceLogFileResponse;
import com.friendly.commons.models.device.response.DeviceTracesResponse;
import com.friendly.commons.models.device.response.DevicesResponse;
import com.friendly.commons.models.device.response.DiagnosticInterfacesResponse;
import com.friendly.commons.models.device.response.DiagnosticsTypeFiltersResponse;
import com.friendly.commons.models.device.response.FileInstancesResponse;
import com.friendly.commons.models.device.response.FileNamesResponse;
import com.friendly.commons.models.device.response.FileTypeFiltersResponse;
import com.friendly.commons.models.device.response.FirmwareStatus;
import com.friendly.commons.models.device.response.GetNewParams;
import com.friendly.commons.models.device.response.ItemTaskIdsResponse;
import com.friendly.commons.models.device.response.ManufacturersResponse;
import com.friendly.commons.models.device.response.MonitoringDetailsResponse;
import com.friendly.commons.models.device.response.RpcMethodsResponse;
import com.friendly.commons.models.device.response.RssiHostsResponse;
import com.friendly.commons.models.device.response.TaskIdsResponse;
import com.friendly.commons.models.device.response.TaskKeyResponse;
import com.friendly.commons.models.device.response.UserExperiencePing;
import com.friendly.commons.models.device.response.UserExperienceRebootResetEvents;
import com.friendly.commons.models.device.response.UserExperienceWifiEvents;
import com.friendly.commons.models.device.rpc.CustomRpc;
import com.friendly.commons.models.device.software.SoftwareInstallRequest;
import com.friendly.commons.models.device.software.SoftwareUnInstallRequests;
import com.friendly.commons.models.device.software.SoftwareUpdateRequest;
import com.friendly.commons.models.device.tools.ReplaceRequest;
import com.friendly.commons.models.device.tools.TraceStatus;
import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.request.LongIdsRequest;
import com.friendly.commons.models.tabs.DeviceTabViewBody;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.device.activity.service.DeviceActivityService;
import com.friendly.services.device.activity.service.TaskService;
import com.friendly.services.device.diagnostics.service.DeviceDiagnosticsService;
import com.friendly.services.device.history.service.DeviceHistoryService;
import com.friendly.services.device.info.service.DeviceMonitoringService;
import com.friendly.services.device.info.service.DeviceService;
import com.friendly.services.device.info.service.DeviceSettingService;
import com.friendly.services.device.info.service.DeviceSoftwareService;
import com.friendly.services.device.info.service.DeviceToolsService;
import com.friendly.services.device.method.service.DeviceMethodService;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.provision.service.DeviceProvisionService;
import com.friendly.services.device.provision.service.DeviceRpcService;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.filemanagement.service.DeviceFileService;
import com.friendly.services.productclass.service.ProductClassGroupService;
import com.friendly.services.qoemonitoring.service.QoeDeviceService;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.uiservices.customization.DeviceTabService;
import com.friendly.services.uiservices.frame.service.DeviceFrameService;
import com.friendly.services.uiservices.system.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

/**
 * Controller that exposes an API to interact with Device
 * <p>
 * This controller is primarily a wrapper around the Device
 * </p>
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@RestController
@Api(value = "Operations with device")
@RequestMapping("iotw/Device")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceController extends BaseController {

    DeviceService deviceService;
    DeviceTabService tabService;
    DeviceActivityService deviceActivityService;
    DeviceFileService deviceFileService;
    DeviceDiagnosticsService diagnosticsService;
    DeviceMonitoringService monitoringService;
    DeviceProvisionService provisionService;
    DeviceRpcService rpcService;
    DeviceSettingService settingService;
    DeviceFrameService frameService;
    DeviceToolsService toolsService;
    DeviceMethodService deviceMethodService;
    ParameterService parameterService;
    DeviceSoftwareService softwareService;
    QoeDeviceService qoeDeviceService;

    TaskService taskService;
    ProductClassGroupService productClassGroupService;
    SystemService systemService;
    TemplateService templateService;
    DeviceHistoryService deviceHistoryService;


    public DeviceController(@NonNull AlertProvider alertProvider,
                            @NonNull DeviceService deviceService,
                            @NonNull DeviceTabService tabService,
                            @NonNull DeviceActivityService deviceActivityService,
                            @NonNull DeviceFileService deviceFileService,
                            @NonNull DeviceDiagnosticsService diagnosticsService,
                            @NonNull DeviceMonitoringService monitoringService,
                            @NonNull DeviceProvisionService provisionService,
                            @NonNull DeviceRpcService rpcService,
                            @NonNull DeviceSettingService settingService,
                            @NonNull DeviceFrameService frameService,
                            @NonNull DeviceToolsService toolsService,
                            @NonNull DeviceMethodService deviceMethodService,
                            @NonNull ParameterService parameterService,
                            @NonNull DeviceSoftwareService softwareService,
                            @NonNull QoeDeviceService qoeDeviceService, TaskService taskService, ProductClassGroupService productClassGroupService, SystemService systemService, TemplateService templateService, DeviceHistoryService deviceHistoryService) {
        super(alertProvider);
        this.deviceService = deviceService;
        this.tabService = tabService;
        this.deviceActivityService = deviceActivityService;
        this.deviceFileService = deviceFileService;
        this.diagnosticsService = diagnosticsService;
        this.monitoringService = monitoringService;
        this.provisionService = provisionService;
        this.rpcService = rpcService;
        this.settingService = settingService;
        this.frameService = frameService;
        this.toolsService = toolsService;
        this.deviceMethodService = deviceMethodService;
        this.parameterService = parameterService;
        this.softwareService = softwareService;
        this.qoeDeviceService = qoeDeviceService;
        this.taskService = taskService;
        this.productClassGroupService = productClassGroupService;
        this.systemService = systemService;
        this.templateService = templateService;
        this.deviceHistoryService = deviceHistoryService;
    }

    /**
     * Get manufacturer names
     *
     * @param token authorization from header
     */
    @ApiOperation(value = "Get manufacturer names and models")
    @PostMapping("manufacturers")
    public ManufacturersResponse getManufacturers(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody(required = false) final ProtocolTypeRequest request) {

        return productClassGroupService.getManufacturerNames(token, request);
    }

    @ApiOperation(value = "Get Devices by view")
    @PostMapping("items")
    public FTPage<Device> getDeviceByList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                          @RequestBody final DeviceExtParamsBody params) {

        return deviceService.getDevicesByList(token, params);
    }

    @ApiOperation(value = "Get Devices by account info fields")
    @PostMapping("items/subscriber")
    public DevicesResponse getDeviceByAccountInfo(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) String token,
                                                  @RequestBody AccountInfoRequest params) {
        return deviceService.getDeviceByAccountInfo(token, params);
    }

    @ApiOperation(value = "Add Device")
    @PutMapping("item")
    public void addDevice(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                          @RequestBody final AddDeviceRequest device) {
        deviceService.createDevice(token, device);
    }

    @ApiOperation(value = "Delete Device")
    @DeleteMapping("items")
    public void deleteDevice(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                             @RequestHeader(IOT_AUTH_HEADER) final String token,
                             @RequestBody final LongIdsRequest request) {

        deviceService.deleteDevice(token, request.getIds());
    }

    @ApiOperation(value = "Get Device information")
    @PostMapping("deviceInfo")
    public DeviceInfoResponse getDeviceInfo(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                            @RequestBody final LongIdRequest request) {

        return deviceService.getDeviceInfo(token, request.getId());
    }

    @ApiOperation(value = "Account Info")
    @PostMapping("accountInfo")
    public AccountInfo getAccountInfo(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                      @RequestBody final LongIdRequest request) {

        return deviceService.getAccountInfo(token, request.getId());
    }

    @ApiOperation(value = "Update Account Info")
    @PutMapping("accountInfo")
    public AccountInfo updateAccountInfo(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody final AccountInfoBody accountInfo) {

        return deviceService.updateAccountInfo(token, accountInfo);
    }

    @ApiOperation(value = "Get tasks by device")
    @PostMapping("tasks")
    public TaskList getTasks(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                             @RequestHeader(IOT_AUTH_HEADER) final String token,
                             @RequestBody final LongIdRequest request) {

        return taskService.getTasks(token, request.getId());
    }

    @ApiOperation(value = "Get device activity")
    @PostMapping("activity")
    public FTPage<DeviceActivity> getDeviceActivity(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                    @RequestBody final DeviceActivityBody deviceActivity) {

        return deviceActivityService.getDeviceActivity(token, deviceActivity);
    }

    @ApiOperation(value = "Get device activity task names")
    @PostMapping("activity/taskNames")
    public DeviceActivityTaskNamesResponse getDeviceActivityTaskNames(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                      @RequestBody final LongIdRequest request) {

        return deviceActivityService.getDeviceActivityTaskNames(token, request.getId());
    }

    @ApiOperation(value = "Delete device activity task")
    @DeleteMapping("activity")
    public void deleteDeviceActivityTask(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody final DeviceActivityTaskBody deviceActivity) {

        deviceActivityService.deleteDeviceActivity(token, deviceActivity);
    }

    @ApiOperation(value = "Get device history activity types")
    @PostMapping("history/types")
    public DeviceHistoryActivityTypesResponse getDeviceHistoryActivityTypes(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                            @RequestBody final LongIdRequest request) {

        return deviceActivityService.getDeviceHistoryActivityTypes(token, request.getId());
    }

    @ApiOperation(value = "Get device history")
    @PostMapping("history")
    public FTPage<DeviceHistory> getDeviceHistory(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final DeviceHistoryBody deviceHistory) {

        return deviceActivityService.getDeviceHistory(token, deviceHistory);
    }

    @ApiOperation(value = "Get device history details")
    @PostMapping("history/details")
    public DeviceHistoryDetailsResponse getDeviceHistoryDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                @RequestBody final DeviceHistoryDetailsBody deviceHistory) {

        return deviceActivityService.getDeviceHistoryDetails(token, deviceHistory);
    }

    @ApiOperation(value = "Delete device history")
    @DeleteMapping("history")
    public void deleteDeviceHistory(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                    @RequestBody final DeleteDeviceHistoryBody deviceHistory) {

        deviceActivityService.deleteDeviceHistory(token, deviceHistory);
    }

    @ApiOperation(value = "Get device log")
    @PostMapping("log")
    public DeviceLog getDeviceLog(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                  @RequestBody final LongIdRequest request) {

        return deviceActivityService.getDeviceLog(token, request.getId());
    }

    @ApiOperation(value = "Get device's files")
    @PostMapping("files")
    public FTPage<DeviceFile> getDeviceFiles(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                             @RequestHeader(IOT_AUTH_HEADER) final String token,
                                             @RequestBody final DeviceFilesBody deviceFiles) {

        return deviceFileService.getDeviceFile(token, deviceFiles);
    }

    @ApiOperation(value = "Get instance paths for device")
    @PostMapping("file/instancePaths")
    public FileNamesResponse getFilePaths(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                          @RequestBody final GetTargetFileNameBody body) {

        return deviceFileService.getTargetFileNames(token, body);
    }

    @ApiOperation(value = "Get file types filter")
    @PostMapping("file/types")
    public FileTypeFiltersResponse getFileTypesFilter(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                      @RequestBody final FileTypesFilterBody fileTypesFilter) {

        return deviceFileService.getFileTypes(token, fileTypesFilter);
    }

    @ApiOperation(value = "Get upload file instances")
    @PostMapping("file/instances")
    public FileInstancesResponse getFileUploadInstances(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final FileUploadInstancesBody fileUploadInstancesBody) {

        return deviceFileService.getInstances(token, fileUploadInstancesBody);
    }

    @ApiOperation(value = "Get download file names")
    @PostMapping("file/fileNames")
    public FileNamesResponse getFileDownloadNames(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final FileDownloadNamesBody fileDownloadNamesBody) {

        return deviceFileService.getFileNames(token, fileDownloadNamesBody);
    }

    @ApiOperation(value = "Add file")
    @PutMapping("file")
    public void addFile(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                        @RequestBody final AddFileBody addFileBody) {

        deviceFileService.addFile(token, addFileBody);
    }

    @ApiOperation(value = "Get file details")
    @PostMapping("file/details")
    public DownloadFileDetails getProvisionDetail(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final LongIdRequest request) {

        return deviceFileService.getFileDetails(token, request);
    }

    @ApiOperation(value = "Delete files")
    @DeleteMapping("files")
    public void deleteFile(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                           @RequestBody final DeleteFileBody deleteFileBody) {

        deviceFileService.deleteFile(token, deleteFileBody);
    }

    @ApiOperation(value = "Get device diagnostics list")
    @PostMapping("diagnostics")
    public FTPage<DeviceDiagnostics> getDeviceDiagnostics(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                          @RequestBody final DeviceDiagnosticsBody deviceDiagnosticsBody) {

        return diagnosticsService.getDeviceDiagnostics(token, deviceDiagnosticsBody);
    }

    @ApiOperation(value = "Add device diagnostic")
    @PutMapping("diagnostic")
    public TaskKeyResponse addDeviceDiagnostic(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                               @RequestBody final AddDeviceDiagnosticBody addDeviceDiagnosticBody) {

        return diagnosticsService.addDiagnostic(token, addDeviceDiagnosticBody);
    }

    @ApiOperation(value = "Get device diagnostic details")
    @PostMapping("diagnostic/details")
    public DiagnosticDetails getDiagnosticDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final LongIdRequest request) {

        return diagnosticsService.getDiagnosticDetails(token, request.getId());
    }

    @ApiOperation(value = "Get device diagnostics types")
    @PostMapping("diagnostic/types")
    public DiagnosticsTypeFiltersResponse getDeviceDiagnostics(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                               @RequestBody final LongIdRequest request) {

        return diagnosticsService.getDiagnosticTypes(token, request.getId());
    }

    @ApiOperation(value = "Get device diagnostics interfaces")
    @PostMapping("diagnostic/interfaces")
    public DiagnosticInterfacesResponse getDiagnosticsInterfaces(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                 @RequestBody final LongIdRequest request) {

        return diagnosticsService.getDiagnosticsInterfaces(token, request.getId());
    }

    @ApiOperation(value = "Delete device diagnostics")
    @DeleteMapping("diagnostics")
    public void deleteDeviceDiagnostics(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                        @RequestBody final DeleteDeviceDiagnosticsBody diagnosticsBody) {

        diagnosticsService.deleteDiagnostics(token, diagnosticsBody);
    }

    @ApiOperation(value = "Get device monitoring list")
    @PostMapping("monitoring")
    public MonitoringDetailsResponse getMonitoring(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final LongIdRequest request) {

        return monitoringService.getDeviceMonitoring(token, request.getId());
    }

    @ApiOperation(value = "Add device monitoring parameters")
    @PutMapping("monitoring")
    public MonitoringDetailsResponse addMonitoringParams(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final AddMonitoringParamsBody monitoringParamsBody) {

        return monitoringService.addDeviceMonitoring(token, monitoringParamsBody);
    }

    @ApiOperation(value = "Delete device monitoring parameters")
    @DeleteMapping("monitoring")
    public void deleteMonitoringParams(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody final AddMonitoringParamsBody paramsBody) {

        monitoringService.deleteDeviceMonitoring(token, paramsBody);
    }

    @ApiOperation(value = "Get device monitoring graph")
    @PostMapping("monitoring/graph")
    public MonitoringGraph getMonitoringGraph(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                                              @RequestBody final MonitoringGraphBody monitoringGraphBody) {

        return monitoringService.getMonitoringGraph(token, monitoringGraphBody);
    }

    @ApiOperation(value = "Start/Stop monitoring graph")
    @PutMapping("monitoring/graph")
    public void startStopMonitoringGraph(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody final StartStopMonitoringGraphBody body) {

        monitoringService.startStopMonitoringGraph(token, body);
    }


    @ApiOperation(value = "Get device provision tabs")
    @PostMapping("provision")
    public FTPage<AbstractProvision> getDeviceProvision(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final DeviceProvisionBody body) {

        return provisionService.getDeviceProvision(token, body);
    }

    @ApiOperation(value = "Edit device provision")
    @PutMapping("provision")
    public void editDeviceProvision(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                    @RequestBody final EditDeviceProvisionBody body) {

        provisionService.updateDeviceProvisions(token, body);
    }

    @ApiOperation(value = "Delete device provisions")
    @DeleteMapping("provision")
    public void deleteDeviceProvisions(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody final DeleteDeviceProvisionsBody body) {

        provisionService.deleteProvisions(token, body);
    }

    @ApiOperation(value = "Get provision details")
    @PostMapping("provision/details")
    public DownloadProvisionDetails getProvisionDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final DownloadProvisionDetailsBody body) {

        return provisionService.getProvisionDetails(token, body);
    }


    @ApiOperation(value = "Get custom RPCs")
    @PostMapping("customRPC")
    public FTPage<CustomRpc> getCustomRpc(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                          @RequestBody final CustomRpcBody body) {

        return rpcService.getCustomRpc(token, body);
    }

    @ApiOperation(value = "Get custom RPC methods")
    @PostMapping("customRPC/methods")
    public RpcMethodsResponse getCustomRpcMethods(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final LongIdRequest request) {

        return rpcService.getRpcMethods(token, request.getId());
    }

    @ApiOperation(value = "Add custom RPC")
    @PutMapping("customRPC")
    public ItemTaskIdsResponse addCustomRpc(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                            @RequestBody final AddCustomRpcBody body) {

        return rpcService.addCustomRpc(token, body);
    }

    @ApiOperation(value = "Delete custom RPC")
    @DeleteMapping("customRPC")
    public void deleteCustomRpc(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                @RequestBody final DeleteCustomRpcBody body) {

        rpcService.deleteCustomRpc(token, body);
    }

    @ApiOperation(value = "Get Device parameters tree")
    @PostMapping("parameters")
    public DeviceObjectsResponse getDeviceParameters(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final DeviceParametersBody body) {

        return settingService.getDeviceParameters(token, body);
    }

    @ApiOperation(value = "Get current parameters from device")
    @PutMapping("parameters/current")
    public ItemTaskIdsResponse getCurrentDeviceParameters(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                          @RequestBody final CurrentDeviceParametersBody body) {

        return settingService.getCurrentDeviceParams(token, body);
    }

    @ApiOperation(value = "Update Device parameters")
    @PutMapping("parameters")
    public ItemTaskIdsResponse updateDeviceParameters(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                      @RequestBody final UpdateDeviceParamsBody body) {

        return settingService.updateDeviceParams(token, body);
    }

    @ApiOperation(value = "Add Device object")
    @PutMapping("parameters/object")
    public ItemTaskIdsResponse addDeviceObject(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                               @RequestBody final AddObjectBody body) {

        return settingService.addParamObject(token, body);
    }

    @ApiOperation(value = "Delete Device objects")
    @DeleteMapping("parameters/object")
    public void deleteDeviceObjects(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                    @RequestBody final DeleteDeviceObjectsBody body) {

        settingService.deleteParamObjects(token, body);
    }

    @ApiOperation(value = "Get Device setting tabs")
    @PostMapping("tabs")
    public DeviceTabsResponse getDeviceTabs(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                            @RequestBody final LongIdRequest request) {

        return tabService.getTabs(token, request.getId());
    }

    @ApiOperation(value = "Get Device setting tab")
    @PostMapping("tabView")
    public DeviceObjectsSimpleResponse getDeviceTabView(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final DeviceTabViewBody body) {

        return tabService.getDeviceTabView(token, body);
    }

    @ApiOperation(value = "Get Device setting Simplified view")
    @PostMapping("simplifiedView")
    public DeviceSimplifiedParamsResponse getDeviceSimplifiedView(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                  @RequestBody final LongIdRequest request) {

        return settingService.getDeviceSimplifiedView(token, request.getId());
    }

    @ApiOperation(value = "Get MAC filtering")
    @PostMapping("macFilter")
    public DeviceMacFilter getMacFiltering(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                                           @RequestBody final LongIdRequest request) {

        return frameService.getMacFilter(token, request.getId());
    }

    @ApiOperation(value = "Add MAC filter")
    @PutMapping("macFilter")
    public TaskIdsResponse addMacFilter(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                        @RequestBody final MacFilterBody body) {

        return frameService.updateMacFilter(token, body);
    }

    @ApiOperation(value = "Get device status connection to ACS")
    @PostMapping("status")
    public DeviceStatusResponse getDeviceStatus(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                @RequestBody final LongIdRequest request) {

        return frameService.getDeviceConnectionStatus(token, request.getId());
    }

    @ApiOperation(value = "Get device status details")
    @PostMapping("status/details")
    public DeviceStatusDetails getDeviceStatusCounts(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final LongIdRequest request) {

        return frameService.getDeviceStatusDetails(token, request.getId());
    }

    @ApiOperation(value = "Get config")
    @PostMapping("config")
    public DeviceConfigsResponse getDeviceConfig(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                 @RequestBody final DeviceConfigTypeRequest request) {

        return settingService.getDeviceConfig(token, request.getType());
    }

    @ApiOperation(value = "View Device Trace")
    @PostMapping("tools/trace")
    public DeviceTracesResponse viewDeviceTrace(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                @RequestBody final LongIdRequest request) {

        return toolsService.getTraceLog(token, request.getId());
    }

    @ApiOperation(value = "Get Device Trace log file")
    @PostMapping("tools/trace/txt")
    public DeviceTraceLogFileResponse deviceTraceTxt(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final TraceTxtBody body) {

        return toolsService.getTraceLogTxt(token, body);
    }

    @ApiOperation(value = "Get Device Trace Status")
    @PostMapping("tools/traceStatus")
    public TraceStatus getDeviceTraceStatus(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                            @RequestBody final LongIdRequest request) {

        return toolsService.getTraceStatus(token, request.getId());
    }

    @ApiOperation(value = "Start/Stop Device Trace")
    @PutMapping("tools/trace")
    public void startStopTrace(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody final StartStopTraceBody body) {

        toolsService.startStopDeviceTracing(token, body);
    }

    @ApiOperation(value = "Delete Device Trace Log")
    @DeleteMapping("tools/trace")
    public void deleteDeviceTrace(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                  @RequestBody final DeviceTraceBody body) {

        toolsService.deleteTrace(token, body);
    }

    @ApiOperation(value = "Start ping to device")
    @PutMapping("tools/ping")
    public void ping(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                     @RequestBody final LongIdRequest request) {

        toolsService.pingDevice(token, request.getId());
    }


    @ApiOperation(value = "Get ping results for device")
    @PostMapping("tools/pingResult")
    public String pingResult(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                             @RequestHeader(IOT_AUTH_HEADER) final String token,
                             @RequestBody final LongIdRequest request) {

        return toolsService.getPingResult(token, request.getId());
    }

    @ApiOperation(value = "Start trace to device")
    @PutMapping("tools/traceRoute")
    public void traceRoute(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                           @RequestBody final LongIdRequest request) {

        toolsService.traceRoute(token, request.getId());
    }

    @ApiOperation(value = "Get trace results for device")
    @PostMapping("tools/traceRouteResult")
    public String traceRouteResult(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @RequestBody final LongIdRequest request) {

        return toolsService.getTraceRouteResult(token, request.getId());
    }

    @ApiOperation(value = "Reprovision")
    @PutMapping("tools/reprovision")
    public ItemTaskIdsResponse reprovision(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                                           @RequestBody final LongIdRequest request) {

        return toolsService.reprovision(token, request.getId());
    }

    @ApiOperation(value = "Reboot device")
    @PutMapping("tools/reboot")
    public ItemTaskIdsResponse reboot(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                      @RequestBody final LongIdRequest request) {

        return toolsService.rebootDevice(token, request.getId());
    }

    @ApiOperation(value = "Factory reset")
    @PutMapping("tools/factoryReset")
    public ItemTaskIdsResponse factoryReset(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                            @RequestBody final LongIdRequest request) {

        return toolsService.factoryReset(token, request.getId());
    }

    @ApiOperation(value = "Get device services")
    @PostMapping("tools/services")
    public DeviceServicesResponse getDeviceServices(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                    @RequestBody final ProtocolTypeRequest request) {

        return toolsService.getDeviceServices(token, request.getProtocolType());
    }

    @ApiOperation(value = "Replace a device")
    @PutMapping("tools/replace")
    public ItemTaskIdsResponse replaceDevice(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                             @RequestHeader(IOT_AUTH_HEADER) final String token,
                                             @RequestBody final ReplaceRequest replaceRequest) {

        return toolsService.replaceDevice(token, replaceRequest);
    }

    @ApiOperation(value = "Get Application ports")
    @PostMapping("ports/appPorts")
    public AppPortsResponse getAppNames(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                        @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return systemService.getAppPorts(token);
    }

    @ApiOperation(value = "Get Device methods")
    @PostMapping("methods")
    public DeviceMethodSResponse getDeviceMethods(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final LongIdRequest request) {

        return deviceMethodService.getDeviceMethods(request.getId(), token);
    }

    @ApiOperation(value = "Get active connection for device")
    @PostMapping("activeConnection")
    public ActiveConnectionResponse activeConnection(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final LongIdRequest request) {

        return parameterService.getActiveConnection(request.getId(), token);
    }

    @ApiOperation(value = "Add device software install")
    @PutMapping("software/install")
    public TaskKeyResponse addDeviceSoftwareInstall(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                    @RequestBody final SoftwareInstallRequest request) {

        return softwareService.install(token, request);
    }

    @ApiOperation(value = "Add device software update")
    @PutMapping("software/update")
    public TaskKeyResponse addDeviceSoftwareUpdate(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final SoftwareUpdateRequest request) {

        return softwareService.update(token, request);
    }

    @ApiOperation(value = "Add device software uninstall")
    @PutMapping("software/uninstall")
    public TaskKeyResponse addDeviceSoftwareUninstall(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                      @RequestBody final SoftwareUnInstallRequests request) {

        return softwareService.unInstall(token, request);
    }

    @ApiOperation(value = "Update device template")
    @PutMapping("template")
    public void updateDeviceTemplate(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                     @RequestBody final UpdateDeviceTemplateBody body) {

        templateService.updateDeviceTemplate(token, body);
    }

    @ApiOperation(value = "Get a list of new parameters")
    @PostMapping("parameters/template")
    public GetNewParams getListOfNewParams(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                                           @RequestBody final GetNewParamsBody body) {

        return parameterService.getNewParams(token, body);
    }

    @ApiOperation(value = "Invoke device parameter")
    @PutMapping("parameters/invoke")
    public void executeInvokeMethod(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                    @RequestHeader(IOT_AUTH_HEADER) final String token,

                                    @RequestBody final InvokeMethodBody body) {

        rpcService.invokeMethod(token, body);
    }

    @ApiOperation(value = "Delete device parameters")
    @DeleteMapping("parameters")
    public void deleteDeviceParameters(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody final DeleteParametersBody body) {

        settingService.deleteDeviceParameters(token, body);
    }

    @ApiOperation(value = "Invoke wifi auto rescan")
    @PutMapping("wireless/autoScan")
    public void wifiAutoRescan(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody final WifiAautoRescanBody request) {

        settingService.wifiAutoRescan(token, request);
    }

    @ApiOperation(value = "Get Firmware Status")
    @PostMapping("firmwareStatus")
    public FirmwareStatus wifiAutoRescan(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody final LongIdRequest request) {

        return deviceFileService.getFirmwareStatus(token, request);
    }

    @ApiOperation(value = "Get Firmware Status")
    @PutMapping("firmwareStatus")
    public FirmwareStatus updateFirmware(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody final LongIdRequest request) {

        return deviceFileService.updateFirmwareVersion(token, request);
    }


    @ApiOperation(value = "Get user experience ping filters")
    @PostMapping("userExperience/ping")
    public UserExperiencePing getUserExperiencePing(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                    @RequestBody final UserExperienceBody request) {

        return qoeDeviceService.getUserExperiencePingFilters(token, request);
    }

    @ApiOperation(value = "Get user experience memory filters")
    @PostMapping("userExperience/memory")
    public GetQoeDetailsResponse getUserExperienceMemory(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final UserExperienceBody request) {

        return qoeDeviceService.getUserExperienceCpuAndMemoryFilters(token, request, true);
    }

    @ApiOperation(value = "Get user experience cpu filters")
    @PostMapping("userExperience/cpu")
    public GetQoeDetailsResponse getUserExperienceCpu(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                      @RequestBody final UserExperienceBody request) {

        return qoeDeviceService.getUserExperienceCpuAndMemoryFilters(token, request, false);
    }

    @ApiOperation(value = "Get user experience wifi events filters")
    @PostMapping("userExperience/wifiEvents")
    public UserExperienceWifiEvents getUserExperienceWifiEvents(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                @RequestBody final UserExperienceBody request) {

        return qoeDeviceService.getUserExperienceWifiEvents(token, request);
    }

    @ApiOperation(value = "User experience reboot reset events")
    @PostMapping("userExperience/rebootResetEvents")
    public UserExperienceRebootResetEvents userExperienceRebootResetEvents(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                           @RequestBody final LongIdRequest request) {

        return deviceHistoryService.rebootResetEvents(token, request);
    }


    @ApiOperation(value = "Get user experience rssi hosts")
    @PostMapping("userExperience/hostsRSSI")
    public RssiHostsResponse getUserRSSIHosts(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                                              @RequestBody final LongIdRequest request) {

        return qoeDeviceService.getUserExperienceHostsRSSI(token, request);
    }

    @ApiOperation(value = "Get user experience associated hosts")
    @PostMapping("userExperience/associatedHosts")
    public AssociatedHosts getUserAssociatedHosts(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final LongIdRequest request) {

        return qoeDeviceService.getUserExperienceAssociatedHosts(token, request);
    }


    @ApiOperation(value = "Get user experience speed test")
    @PostMapping("userExperience/speedTest")
    public GetSpeedTestResponse  getUserSpeedTest(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final UserExperienceBody request) {

        return qoeDeviceService.getUserExperienceSpeedTest(token, request);
    }



}