package com.friendly.services.productclass;

import com.friendly.services.management.action.dto.request.GetActionListByMonitorIdRequest;
import com.friendly.services.management.action.dto.request.GetByManufAndModelWithOwnerTypeRequest;
import com.friendly.services.management.action.dto.response.ActionMethodsList;
import com.friendly.services.management.action.dto.response.MonitorActionResponse;
import com.friendly.commons.models.device.ModelManufacturerRequest;
import com.friendly.commons.models.device.ProductClassGroup;
import com.friendly.commons.models.device.response.DeviceObjectsResponse;
import com.friendly.commons.models.device.response.DiagnosticInterfacesResponse;
import com.friendly.commons.models.device.response.DiagnosticsTypeFiltersResponse;
import com.friendly.commons.models.device.response.IsExistResponse;
import com.friendly.commons.models.tabs.TemplateParametersBody;
import com.friendly.services.device.diagnostics.service.DeviceDiagnosticsService;
import com.friendly.services.device.info.model.DeploymentUnitDetailsList;
import com.friendly.services.device.info.service.DeviceService;
import com.friendly.services.device.info.service.DeviceSoftwareService;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.management.action.service.ActionService;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileAutomationEventsNamesResponse;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProtocolDetail;
import com.friendly.services.uiservices.customization.DeviceTabService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

@RestController
@Api(value = "Operations with device by model and manufacturer")
@RequestMapping("iotw/Model")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModelController {
    private DeviceService deviceService;
    private TemplateService templateService;
    private DeviceTabService tabService;
    private DeviceDiagnosticsService deviceDiagnosticsService;
    private DeviceSoftwareService softwareService;
    private ActionService actionService;

    @ApiOperation(value = "Get device protocol type/version info")
    @PostMapping("/protocol")
    public DeviceProtocolDetail getDeviceProtocolDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final ProductClassGroup body) {
        return deviceService.getProtocolDetails(body.getManufacturer(), body.getModel(), token);
    }

    @ApiOperation(value = "Get template parameters tree")
    @PostMapping("/template/parameters")
    public DeviceObjectsResponse getTemplateParameters(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                       @RequestBody final TemplateParametersBody request) {

        return templateService.getGroupParameters(token, request);
    }

    @ApiOperation(value = "Check if template exists")
    @PostMapping("/template/isExist")
    public IsExistResponse getTemplateParameters(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                 @RequestBody final ModelManufacturerRequest request) {

        return templateService.checkIfTemplateExists(token, request);
    }

    @ApiOperation(value = "Get device profile events tab details")
    @PostMapping("/device-profile/events")
    public DeviceProfileAutomationEventsNamesResponse getDeviceProfileEventsTabNames(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                                     @RequestBody final ProductClassGroup request) {
        return tabService.getProfileEventsTabNamesByManufAndModel(token, request.getManufacturer(), request.getModel());
    }

    @ApiOperation(value = "Get diagnostics type filters by manufacturer and model")
    @PostMapping("/diagnostic/types")
    public DiagnosticsTypeFiltersResponse getDiagnosticTypesByManufAndModel(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                            @RequestBody final ProductClassGroup request) {
        return deviceDiagnosticsService.getDiagnosticTypesByProductClassGroup(token, request.getManufacturer(), request.getModel());
    }

    @ApiOperation(value = "Get diagnostic interfaces by manufacturer and model")
    @PostMapping("/diagnostic/interfaces")
    public DiagnosticInterfacesResponse getDiagnosticInterfacesByManufAndModel(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                               @RequestBody final ProductClassGroup request){
        return deviceDiagnosticsService.getDiagnosticsInterfacesByGroup(token, request.getManufacturer(), request.getModel());
    }

    @ApiOperation(value = "Get device software details")
    @PostMapping("software/details")
    public DeploymentUnitDetailsList getDeviceSoftwareDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                              @RequestBody final ProductClassGroup request) {

        return softwareService.getDeviceSoftwareDetails(token, request);
    }

    @ApiOperation(value = "Get available action methods")
    @PostMapping("actions/methods")
    public ActionMethodsList getActionMethodList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                 @RequestBody final GetByManufAndModelWithOwnerTypeRequest body) {
        return actionService.getActionMethods(body.getManufacturer(), body.getModel(), body.getType(), token);
    }

    @ApiOperation(value = "Get monitor`s action list")
    @PostMapping("actions")
    public MonitorActionResponse getAutomationActionList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final GetActionListByMonitorIdRequest body) {
        return actionService.getMonitorActionList(body, token);
    }
}
