package com.friendly.services.uiservices.user.controller;

import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.request.RestorePasswordRequest;
import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.user.AllUsersBody;
import com.friendly.commons.models.user.UserRequest;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.commons.models.user.request.UserIdsRequest;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.uiservices.user.UserService;
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

import javax.validation.Valid;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

/**
 * Controller that exposes an API to interact with User
 * <p>
 * This controller is primarily a wrapper around the {@link UserResponse}
 * </p>
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@RestController
@Api(value = "Operations with user")
@RequestMapping("iotw/User")
public class UserController extends BaseController {

    @NonNull
    private final UserService userService;

    public UserController(@NonNull AlertProvider alertProvider,
                          @NonNull UserService userService) {
        super(alertProvider);
        this.userService = userService;
    }

    /**
     * Get all users
     *
     * @return {@link UserResponse} user with matching id
     */
    @ApiOperation(value = "Get all users")
    @PostMapping("/users")
    public FTPage<UserResponse> getAllUsers(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                            @RequestBody AllUsersBody body) {

        return userService.getAllUsers(token, body);
    }

    /**
     * Create/Update user
     *
     * @return {@link UserResponse} created user
     */
    @ApiOperation(value = "Create/Update user")
    @PutMapping("/user")
    public UserResponse createUser(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @Valid @RequestBody final UserRequest user) {

        return userService.createOrUpdateUser(token, user);
    }

    /**
     * Delete user
     */
    @ApiOperation(value = "Delete users bu ids")
    @DeleteMapping("/user")
    public void deleteUsers(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody final UserIdsRequest request) {

        userService.deleteUsers(token, request.getIds());
    }

    /**
     * Get user by token or id if present
     *
     * @param token authorization from header
     * @return {@link UserResponse} current user
     */
    @ApiOperation(value = "Get user")
    @PostMapping("/user")
    public UserResponse getUser(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                @RequestBody(required = false) final LongIdRequest request) {

        return userService.getUser(token, request.getId());
    }

    @ApiOperation(value = "Restore password")
    @PutMapping("/password/restore")
    public boolean restorePassword(@Valid @RequestBody final RestorePasswordRequest restorePasswordRequest) {

        return userService.sendResetPasswordLink(restorePasswordRequest);
    }
}