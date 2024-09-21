package com.friendly.services.uiservices.customization.controller;

import com.friendly.commons.models.device.ProductClassGroup;
import com.friendly.commons.models.device.response.DeviceObjectsSimpleResponse;
import com.friendly.commons.models.device.response.DeviceTabsResponse;
import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.tabs.DeviceTabViewBody;
import com.friendly.commons.models.tabs.ProductClassGroupTabViewBody;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.uiservices.customization.DeviceTabService;
import com.friendly.services.settings.alerts.AlertProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

@RestController
@Api(value = "Operations with device profiles")
@RequestMapping("iotw/tabs")
public class TabController extends BaseController {


    @NonNull
    private final DeviceTabService tabService;


    public TabController(@NonNull AlertProvider alertProvider,
                        @NonNull DeviceTabService tabService) {
        super(alertProvider);
        this.tabService = tabService;
    }

    @ApiOperation(value = "Get Device profile tabs by manufacturer and model")
    @PostMapping("device-profile")
    public DeviceTabsResponse getProfileTabsByManufAndModel(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                            @RequestBody final ProductClassGroup request) {

        return tabService.getProfileTabs(token, request.getManufacturer(), request.getModel());
    }

    @ApiOperation(value = "Get profile parameter tab by manufacturer and model")
    @PostMapping("device-profile/parameters")
    public DeviceObjectsSimpleResponse getProfileParameterManufAndModelTabView(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                               @RequestBody final ProductClassGroupTabViewBody body) {
        return tabService.getProfileParameterManufAndModelTabViewWithTaskType(token, body.getManufacturer(), body.getModel(), body.getTaskType(), body.getTabPath());
    }

    @ApiOperation(value = "Get Device profile tabs by manufacturer and model")
    @PostMapping("/tasks/path")
    public DeviceTabsResponse getProfileTabsByManufAndModelAndTabPath(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                      @RequestBody final ProductClassGroupTabViewBody request) {

        return tabService.getProfileTabsByProductClassAndPath(token, request.getManufacturer(), request.getModel(), request.getTabPath());
    }


//    @ApiOperation(value = "Get setting tab by manufacturer and model")
//    @PostMapping("tabManufAndModelView")
//    public DeviceObjectsSimpleResponse getManufAndModelTabView(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
//                                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
//                                                               @RequestBody final ProductClassGroupTabViewBody body) {
//
//        return tabService.getManufAndModelTabView(token, body.getManufacturer(), body.getModel(), body.getTabPath());
//    }

    @ApiOperation(value = "Get Device setting tab")
    @PostMapping("device/parameters")
    public DeviceObjectsSimpleResponse getDeviceTabView(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final DeviceTabViewBody body) {

        return tabService.getDeviceTabView(token, body);
    }

    @ApiOperation(value = "Get Device setting tabs")
    @PostMapping("device")
    public DeviceTabsResponse getDeviceTabs(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                            @RequestBody final LongIdRequest request) {

        return tabService.getTabs(token, request.getId());
    }


}