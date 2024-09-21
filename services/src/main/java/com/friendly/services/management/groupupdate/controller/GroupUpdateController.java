package com.friendly.services.management.groupupdate.controller;

import com.friendly.commons.models.FTPage;
import com.friendly.services.management.groupupdate.dto.response.DevicesStatusResponse;
import com.friendly.services.management.groupupdate.dto.request.GetGroupUpdateGroups;
import com.friendly.services.management.groupupdate.dto.GroupUpdateCondition;
import com.friendly.services.management.groupupdate.dto.GroupUpdateConditionItem;
import com.friendly.services.management.groupupdate.dto.GroupUpdateFilters;
import com.friendly.services.management.groupupdate.dto.GroupUpdateGroup;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateGroupDetailsResponse;
import com.friendly.services.management.groupupdate.dto.request.GroupUpdateGroupDetailsRequest;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateSerialResponse;
import com.friendly.services.management.groupupdate.dto.response.TargetedDevicesResponse;
import com.friendly.commons.models.request.IntIdRequest;
import com.friendly.commons.models.request.IntIdsRequest;
import com.friendly.services.management.groupupdate.service.GroupUpdateDetailsService;
import com.friendly.services.management.groupupdate.service.GroupUpdateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

@RestController
@Api(value = "Operations with group updates")
@RequestMapping("iotw/GroupUpdate")
@RequiredArgsConstructor
public class GroupUpdateController {

    @NonNull
    private final GroupUpdateService groupUpdateService;
    @NonNull
    private final GroupUpdateDetailsService groupUpdateDetailsService;


    @ApiOperation(value = "Get group for group update")
    @PostMapping("/groups")
    public FTPage<GroupUpdateGroup> getGroupUpdateGroups(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final GetGroupUpdateGroups body) {

        return groupUpdateService.getGroups(token, body);
    }

    @ApiOperation(value = "Activate groups")
    @PostMapping("/activate")
    public boolean activateGroups(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                  @RequestBody final IntIdsRequest body) {

        return groupUpdateService.activateGroups(token, body);
    }

    @ApiOperation(value = "Delete group update groups")
    @DeleteMapping("/groups")
    public boolean deleteGroupUpdateGroups(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                                           @RequestBody final IntIdsRequest body) {
        return groupUpdateService.deleteGroups(token, body);
    }

    @ApiOperation(value = "Stop groups")
    @PostMapping("/stop")
    public boolean stopGroups(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                              @RequestBody final IntIdsRequest body) {

        return groupUpdateService.stopGroups(token, body);
    }

    @ApiOperation(value = "Pause groups")
    @PostMapping("/pause")
    public boolean pauseGroups(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody final IntIdsRequest body) {

        return groupUpdateService.pauseGroups(token, body);
    }

    @ApiOperation(value = "Get a list of condition items")
    @PostMapping("/condition/items")
    public List<GroupUpdateCondition> pauseGroups(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return groupUpdateService.getConditionItems(token);
    }


    @ApiOperation(value = "Get details by groupId")
    @PostMapping("/groups/details")
    public GroupUpdateGroupDetailsResponse details(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final IntIdRequest body) {

        return groupUpdateDetailsService.getDetails(token, body);
    }

    @ApiOperation(value = "Save update group")
    @PutMapping("/groups/details")
    public boolean save(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                        @RequestBody final GroupUpdateGroupDetailsRequest body) {

        return groupUpdateDetailsService.save(token, body);
    }

    @ApiOperation(value = "Get a list of devices")
    @PostMapping("/devices")
    public FTPage<GroupUpdateSerialResponse> getDevices(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                      @RequestBody final GroupUpdateFilters filters) {

        return groupUpdateService.getDevices(token, filters);
    }

    @ApiOperation(value = "Create new group update view")
    @PutMapping("/condition/item")
    public void createNewGroupUpdateView(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody final GroupUpdateConditionItem body) {

        groupUpdateService.createGroupUpdateView(token, body);
    }

    @ApiOperation(value = "Get the number of devices to which the group update activation")
    @PostMapping("/activate/targetedDevices")
    public TargetedDevicesResponse targetedDevices(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                              @RequestBody final IntIdsRequest body) {

        return groupUpdateService.targetedDevices(token, body);
    }

    @ApiOperation(value = "Get devices status list counter")
    @PostMapping("/activate/devices/status")
    public DevicesStatusResponse devicesStatus(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                               @RequestBody final IntIdRequest body) {

        return groupUpdateService.devicesStatus(token, body);
    }
}
