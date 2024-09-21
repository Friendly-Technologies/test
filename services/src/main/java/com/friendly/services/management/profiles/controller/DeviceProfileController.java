package com.friendly.services.management.profiles.controller;

import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.device.ProductClassGroup;
import com.friendly.commons.models.device.response.DeviceTabsResponse;
import com.friendly.commons.models.view.response.ConditionsResponse;
import com.friendly.commons.models.view.response.DeviceColumnsResponse;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.uiservices.customization.DeviceTabService;
import com.friendly.services.management.profiles.entity.GetConditionBody;
import com.friendly.services.management.profiles.entity.GetConditionFiltersBody;
import com.friendly.services.management.profiles.entity.GetConditionsBody;
import com.friendly.services.management.profiles.entity.GetDeviceProfilesBody;
import com.friendly.services.management.profiles.entity.GroupConditionItem;
import com.friendly.services.management.profiles.entity.GroupConditionTypes;
import com.friendly.services.management.profiles.entity.SimpleConditions;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfile;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileBody;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileDetailRequest;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileDetailResponse;
import com.friendly.services.management.action.service.ActionService;
import com.friendly.services.management.profiles.service.DeviceProfileService;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.uiservices.view.ColumnKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

@RestController
@Api(value = "Operations with device profiles")
@RequestMapping("iotw/DeviceProfile")
public class DeviceProfileController extends BaseController {

    @NonNull
    private final DeviceProfileService deviceProfilerService;

    @NonNull
    private final ActionService actionService;

    @NonNull
    private final DeviceTabService tabService;


    public DeviceProfileController(@NonNull AlertProvider alertProvider,
                                   @NonNull DeviceProfileService deviceProfilerService,
                                   @NonNull ActionService actionService,
                                   @NonNull DeviceTabService tabService) {
        super(alertProvider);
        this.deviceProfilerService = deviceProfilerService;
        this.actionService = actionService;
        this.tabService = tabService;
    }


    @ApiOperation(value = "Get device profiles")
    @PostMapping("/profiles")
    public FTPage<DeviceProfile> getDeviceByList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                 @RequestBody final GetDeviceProfilesBody body) {

        return deviceProfilerService.getProfiles(token, body);
    }

    @ApiOperation(value = "Activate a device profile")
    @PostMapping("/activate")
    public boolean activateDeviceProfile(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                               @RequestBody final DeviceProfileBody body) {

        return deviceProfilerService.activateDeviceProfile(token, body);
    }

    @ApiOperation(value = "Deactivate a device profile")
    @PostMapping("/deactivate")
    public boolean deactivateDeviceProfile(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                               @RequestBody final DeviceProfileBody body) {

        return deviceProfilerService.deactivateDeviceProfile(token, body);
    }

    @ApiOperation(value = "Delete a device profile")
    @DeleteMapping("/profiles")
    public boolean deleteDeviceProfiles(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                                           @RequestBody final DeviceProfileBody body) {

        return deviceProfilerService.deleteDeviceProfile(token, body);
    }

    @ApiOperation(value = "Get device conditions list")
    @PostMapping("/condition/items")
    public SimpleConditions getConditionsList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final GetConditionsBody body) {

        return deviceProfilerService.getConditions(token, body);
    }

    @ApiOperation(value = "Get device condition")
    @PostMapping("/condition/item")
    public GroupConditionItem getCondition(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                                              @RequestBody final GetConditionBody body) {

        return deviceProfilerService.getCondition(token, body);
    }


    @ApiOperation(value = "Get device conditions type list")
    @PostMapping("/condition/types")
    public GroupConditionTypes getConditionsTypeList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                                              @RequestBody final GetConditionsBody body) {

        return deviceProfilerService.getConditionTypes(token, body);
    }

    @ApiOperation(value = "Get device conditions column list")
    @PostMapping("/condition/column/items")
    public DeviceColumnsResponse getConditionsTypeList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                       @RequestBody final GetConditionFiltersBody body) {

        return deviceProfilerService.getConditionFilters(token, body);
    }

    @ApiOperation(value = "Get device conditions comparisons list")
    @PostMapping("/condition/column/compare")
    public ConditionsResponse getDeviceColumnFilters(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final ColumnKey columnKey) {

        return deviceProfilerService.getConditionComparisonsByColumn(token, columnKey.getColumnKey());
    }


    @ApiOperation(value = "Add/edit device condition")
    @PutMapping("/condition/item")
    public GroupConditionItem getCondition(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                                           @RequestBody final GroupConditionItem body) {

        return deviceProfilerService.saveCondition(token, body);
    }

    @ApiOperation(value = "Get profile details")
    @PostMapping("/profiles/details")
    public DeviceProfileDetailResponse getProfileDetail(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final DeviceProfileBody body) {

        return deviceProfilerService.getProfileDetails(token, body.getId());
    }

    @ApiOperation(value = "Save or update profile details")
    @PutMapping("/profiles/details")
    public DeviceProfileDetailResponse saveProfileDetail(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final DeviceProfileDetailRequest body) {

        return deviceProfilerService.saveOrUpdateProfileDetails(token, body);
    }


    @ApiOperation(value = "Get Device profile tabs by manufacturer and model")
    @PostMapping("tabsByManufAndModel")
    public DeviceTabsResponse getProfileTabsByManufAndModel(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                            @RequestBody final ProductClassGroup request) {

        return tabService.getProfileTabs(token, request.getManufacturer(), request.getModel());
    }

    @ApiOperation(value = "Get Device profile tabs by profile id")
    @PostMapping("tabsByProfileId")
    public DeviceTabsResponse getProfileTabsById(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                 @RequestBody final DeviceProfileBody body) {

        return deviceProfilerService.getProfileTabs(token, body.getId());
    }
}