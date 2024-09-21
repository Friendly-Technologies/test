package com.friendly.services.uiservices.auth;

import com.friendly.commons.models.auth.request.AuthRequest;
import com.friendly.commons.models.auth.response.AuthResponse;
import com.friendly.commons.models.auth.response.LogoutReasonTypeResponse;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.settings.alerts.AlertProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

@RestController
@Api(value = "Authorization operations")
@RequestMapping(value = "iotw/Auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController extends BaseController {

    final AuthService authService;

    @Value("${jwt.token.expired}")
    long expirationTimeMs;

    public AuthController(@NonNull AlertProvider alertProvider,
                          @NonNull AuthService authService) {
        super(alertProvider);
        this.authService = authService;
    }

    @ApiOperation(value = "Get current Authorization Type")
    @PostMapping("/type")
    public ResponseEntity<AuthType> getCurrentAuthType() {
        return ResponseEntity.ok(authService.getCurrentAuthType());
    }

    @ApiOperation(value = "Login to system. Get token")
    @PostMapping("login")
    public AuthResponse login(@RequestBody AuthRequest request, HttpServletRequest httpServletRequest) {
        return authService.getAuthResponse(request, httpServletRequest, expirationTimeMs);
    }

    @ApiOperation(value = "Logout from system")
    @PostMapping("logout")
    public LogoutReasonTypeResponse logout(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                           HttpServletRequest httpServletRequest) {

        return authService.getLogoutReasonTypeResponse(token, httpServletRequest);
    }

    @ApiOperation(value = "Reset password")
    @PutMapping("/password/reset")
    public AuthResponse resetPassword(@Valid @RequestBody final AuthRequest request,
                                      HttpServletRequest httpServletRequest) {
        return authService.getAuthResponse(request, httpServletRequest, expirationTimeMs);
    }
}
