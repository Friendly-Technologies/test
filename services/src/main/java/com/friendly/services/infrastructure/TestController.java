package com.friendly.services.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendly.commons.models.settings.SnmpVersionType;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.settings.snmpserver.sender.SnmpSender;
import com.friendly.services.settings.usergroup.UserGroupService;
import com.friendly.services.infrastructure.utils.startup.SettingInitializer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@RestController
@Api(value = "Test Controller")
@RequestMapping("iotw/Test")
public class TestController extends BaseController {

    @Value("${server.path}")
    private String appPath;

    @NonNull
    private final ObjectMapper mapper;

    @NonNull
    private final UserGroupService userGroupService;


    public TestController(@NonNull AlertProvider alertProvider,
                          @NonNull ObjectMapper objectMapper,
                          @NonNull UserGroupService userGroupService, @NonNull SettingInitializer settingInitializer) {
        super(alertProvider);
        this.mapper = objectMapper;
        this.userGroupService = userGroupService;
    }

    @ApiOperation(value = "Send trap V1")
    @PostMapping("/trapV1")
    public void trapV1(@RequestBody final String message) {

        final SnmpSender sender = new SnmpSender("127.0.0.1", "162", "public", SnmpVersionType.V1);
        sender.sendSnmpTrap(message);
    }

    @ApiOperation(value = "Send trap V2")
    @PostMapping("/trapV2")
    public void trapV2(@RequestBody final String message) {

        final SnmpSender sender = new SnmpSender("127.0.0.1", "162", "public", SnmpVersionType.V2);
        sender.sendSnmpTrap(message);
    }

}