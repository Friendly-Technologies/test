package com.friendly.services.uiservices.system.controller;

import com.friendly.commons.models.system.ServerTimeResponse;
import com.friendly.commons.models.system.response.DateFormatsResponse;
import com.friendly.commons.models.system.response.LocalesResponse;
import com.friendly.commons.models.system.response.TimeFormatsResponse;
import com.friendly.commons.models.system.response.TimeZonesResponse;
import com.friendly.commons.models.system.response.VersionResponse;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.uiservices.system.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

/**
 * Controller that exposes an API to interact with System
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@RestController
@Api(value = "System operations")
@RequestMapping("iotw/System")
public class SystemController extends BaseController {

    @NonNull
    private final SystemService systemService;

    public SystemController(@NonNull AlertProvider alertProvider,
                            @NonNull SystemService systemService) {
        super(alertProvider);
        this.systemService = systemService;
    }

    @ApiOperation(value = "Service version")
    @GetMapping("/version")
    public VersionResponse getVersion() {

        return systemService.version();
    }

    @ApiOperation(value = "Restart service")
    @PostMapping("/restart")
    public void getAllUsers(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token) {

        systemService.restart(token);
    }

    @ApiOperation(value = "Get all locales")
    @PostMapping("/locales")
    public LocalesResponse getLocales(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return systemService.getLocales(token);
    }

    @ApiOperation(value = "Get date formats")
    @PostMapping("/dateFormats")
    public DateFormatsResponse getDateFormats(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return systemService.getDateFormats(token);
    }

    @ApiOperation(value = "Get time formats")
    @PostMapping("/timeFormats")
    public TimeFormatsResponse getTimeFormats(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return systemService.getTimeFormats(token);
    }

    @ApiOperation(value = "Get time zones")
    @PostMapping("/timeZones")
    public TimeZonesResponse getTimeZones(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return systemService.getTimeZones(token);
    }

    @ApiOperation(value = "Get server time")
    @PostMapping("/serverTime")
    public ServerTimeResponse getServerTime(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return systemService.getServerTime(token);
    }

    @ApiOperation(value = "Get app version")
    @PostMapping("/information")
    public Map<String, String> getAppVersion() {
        return systemService.getAppVersion();
    }
}