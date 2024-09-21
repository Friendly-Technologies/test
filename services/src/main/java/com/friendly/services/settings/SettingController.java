package com.friendly.services.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.RequestPageInfo;
import com.friendly.commons.models.device.UnusedModelsResponse;
import com.friendly.commons.models.device.response.ManufacturersResponse;
import com.friendly.commons.models.device.response.TaskIdsResponse;
import com.friendly.commons.models.reports.ActiveSessionsBody;
import com.friendly.commons.models.request.IntIdRequest;
import com.friendly.commons.models.request.IntIdsRequest;
import com.friendly.commons.models.request.LongIdsRequest;
import com.friendly.commons.models.settings.AcsUserBody;
import com.friendly.commons.models.settings.AcsUsersBody;
import com.friendly.commons.models.settings.Alerts;
import com.friendly.commons.models.settings.AlertsResponse;
import com.friendly.commons.models.settings.Connections;
import com.friendly.commons.models.settings.EmailServer;
import com.friendly.commons.models.settings.EmailServers;
import com.friendly.commons.models.settings.NotificationInfo;
import com.friendly.commons.models.settings.RemoveWhiteListBody;
import com.friendly.commons.models.settings.RetrieveMode;
import com.friendly.commons.models.settings.RetrieveModeBody;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.commons.models.settings.SnmpServer;
import com.friendly.commons.models.settings.UserGroupBody;
import com.friendly.commons.models.settings.UserGroupsBody;
import com.friendly.commons.models.settings.WhiteListBody;
import com.friendly.commons.models.settings.acs.AcsLicenses;
import com.friendly.commons.models.settings.acs.AcsUser;
import com.friendly.commons.models.settings.acs.AddAcsLicense;
import com.friendly.commons.models.settings.acs.whitelist.AbstractAcsWhiteList;
import com.friendly.commons.models.settings.acs.whitelist.AcsCheckWhiteList;
import com.friendly.commons.models.settings.acs.whitelist.RemoveSerialFromWhiteList;
import com.friendly.commons.models.settings.acs.whitelist.WhiteListCheckResponse;
import com.friendly.commons.models.settings.acs.whitelist.WhiteListSerialType;
import com.friendly.commons.models.settings.bootstrap.BootstrapLWM2M;
import com.friendly.commons.models.settings.bootstrap.BootstrapLog;
import com.friendly.commons.models.settings.config.AbstractConfigItem;
import com.friendly.commons.models.settings.config.property.UpdateIotPropertyRequest;
import com.friendly.commons.models.settings.iot.BootstrapConfigBody;
import com.friendly.commons.models.settings.iot.BootstrapConfigDetailsBody;
import com.friendly.commons.models.settings.iot.BootstrapLogBody;
import com.friendly.commons.models.settings.iot.BootstrapLogDetailsBody;
import com.friendly.commons.models.settings.iot.DeleteAllBootstrapLogBody;
import com.friendly.commons.models.settings.iot.DeleteBootstrapConfigBody;
import com.friendly.commons.models.settings.iot.DeleteIotResourceBody;
import com.friendly.commons.models.settings.iot.DeleteIotSecurityBody;
import com.friendly.commons.models.settings.iot.IotResourceBody;
import com.friendly.commons.models.settings.iot.IotResourceDetailBody;
import com.friendly.commons.models.settings.iot.Lwm2mSecurityBody;
import com.friendly.commons.models.settings.iot.MqttSecurityBody;
import com.friendly.commons.models.settings.iot.SecurityModesBody;
import com.friendly.commons.models.settings.iot.UspSecurityBody;
import com.friendly.commons.models.settings.iot.request.ProtocolSecurityTypeRequest;
import com.friendly.commons.models.settings.iot.request.ProtocolTabTypeRequest;
import com.friendly.commons.models.settings.iot.response.AbstractConfigPropertiesResponse;
import com.friendly.commons.models.settings.iot.response.BootstrapLogDetailsResponse;
import com.friendly.commons.models.settings.iot.response.IotConfigTabsResponse;
import com.friendly.commons.models.settings.iot.response.MaskTypesResponse;
import com.friendly.commons.models.settings.iot.response.ResourceDetailsResponse;
import com.friendly.commons.models.settings.iot.response.SecurityModeTypesResponse;
import com.friendly.commons.models.settings.iot.response.ServerTypesResponse;
import com.friendly.commons.models.settings.request.AbstractConfigItemsRequest;
import com.friendly.commons.models.settings.request.CheckDependencyRequest;
import com.friendly.commons.models.settings.request.CheckUserGroupRequest;
import com.friendly.commons.models.settings.request.ConfigItemRequest;
import com.friendly.commons.models.settings.request.HardcodedEventRequest;
import com.friendly.commons.models.settings.request.HardcodedEventUrlDeleteRequest;
import com.friendly.commons.models.settings.request.HardcodedEventUrlRequest;
import com.friendly.commons.models.settings.request.LoginsRequest;
import com.friendly.commons.models.settings.request.ServerDetailsRequest;
import com.friendly.commons.models.settings.request.SessionHashesRequest;
import com.friendly.commons.models.settings.request.SnmpServerRequest;
import com.friendly.commons.models.settings.request.WhiteListIpRequest;
import com.friendly.commons.models.settings.request.WhiteListRequest;
import com.friendly.commons.models.settings.resource.AbstractResource;
import com.friendly.commons.models.settings.response.AbstractConfigItemsListResponse;
import com.friendly.commons.models.settings.response.AbstractConfigItemsResponse;
import com.friendly.commons.models.settings.response.AlertEventsResponse;
import com.friendly.commons.models.settings.response.CheckDependencyResponse;
import com.friendly.commons.models.settings.response.CheckUserGroupResponse;
import com.friendly.commons.models.settings.response.FileServers;
import com.friendly.commons.models.settings.response.HardcodedEventsResponse;
import com.friendly.commons.models.settings.response.HardcodedEventsUrlsResponse;
import com.friendly.commons.models.settings.response.SecurityUspMtpModeResponse;
import com.friendly.commons.models.settings.response.SnmpServerResponse;
import com.friendly.commons.models.settings.response.UserGroupsSimpleResponse;
import com.friendly.commons.models.settings.security.AbstractSecurity;
import com.friendly.commons.models.settings.security.SecurityDetailBody;
import com.friendly.commons.models.settings.security.SecurityLWM2MDetails;
import com.friendly.commons.models.settings.security.SecurityMQTTDetails;
import com.friendly.commons.models.settings.security.SecurityUSPDetails;
import com.friendly.commons.models.settings.security.SecurityUSPDetailsRequest;
import com.friendly.commons.models.user.Domain;
import com.friendly.commons.models.user.UserGroupRequest;
import com.friendly.commons.models.user.UserGroupResponse;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.commons.models.user.UserSession;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.device.info.service.DeviceService;
import com.friendly.services.productclass.service.ProductClassGroupService;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityMqttEntity;
import com.friendly.services.settings.acs.AcsConfigService;
import com.friendly.services.settings.acs.AcsLicenseService;
import com.friendly.services.settings.acs.AcsUserService;
import com.friendly.services.settings.acs.AcsWhiteListService;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.settings.alerts.AlertsService;
import com.friendly.services.settings.alerts.NodesListResponse;
import com.friendly.services.settings.bootstrap.BootstrapConfigDetailsExistBody;
import com.friendly.services.settings.bootstrap.BootstrapService;
import com.friendly.services.settings.bootstrap.ConfigService;
import com.friendly.services.settings.bootstrap.ResourceService;
import com.friendly.services.settings.bootstrap.SecurityService;
import com.friendly.services.settings.bootstrap.UspIdentifierExists;
import com.friendly.services.settings.connections.ConnectionsService;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.emailserver.EmailServerService;
import com.friendly.services.settings.events.HardcodedEventsService;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.settings.notification.NotificationService;
import com.friendly.services.device.parameterstree.service.RetrieveModeService;
import com.friendly.services.settings.sessions.SessionService;
import com.friendly.services.settings.snmpserver.SnmpServerService;
import com.friendly.services.settings.usergroup.UserGroupService;
import com.friendly.services.settings.usergroup.model.UserGroupTimestamp;
import com.friendly.services.settings.userinterface.InterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

/**
 * Controller that exposes an API to interact with Settings
 * <p>
 * This controller is primarily a wrapper around the Settings
 * </p>
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@RestController
@Api(value = "Operations with setting")
@RequestMapping("iotw/Setting")
public class SettingController extends BaseController {

    @NonNull
    private final AlertsService alertsService;
    @NonNull
    private final InterfaceService interfaceService;
    @NonNull
    private final UserGroupService userGroupService;
    @NonNull
    private final DomainService domainService;
    @NonNull
    private final ConnectionsService connectionsService;
    @NonNull
    private final FileServerService fileServerService;
    @NonNull
    private final EmailServerService emailServerService;
    @NonNull
    private final SnmpServerService snmpServerService;
    @NonNull
    private final SessionService sessionService;
    @NonNull
    private final AcsLicenseService acsLicenseService;
    @NonNull
    private final AcsConfigService acsConfigService;
    @NonNull
    private final AcsUserService acsUserService;
    @NonNull
    private final AcsWhiteListService whiteListService;
    @NonNull
    private final RetrieveModeService retrieveModeService;
    @NonNull
    private final ProductClassGroupService productClassGroupService;
    @NonNull
    private final DeviceService deviceService;
    @NonNull
    private final ConfigService configService;
    @NonNull
    private final NotificationService notificationService;
    @NonNull
    private final BootstrapService bootstrapService;
    @NonNull
    private final ResourceService resourceService;
    @NonNull
    private final SecurityService securityService;
    @NonNull
    private final HardcodedEventsService hardcodedEventsService;

    public SettingController(@NonNull AlertProvider alertProvider,
                             @NonNull AlertsService alertsService,
                             @NonNull InterfaceService interfaceService,
                             @NonNull UserGroupService userGroupService,
                             @NonNull DomainService domainService,
                             @NonNull ConnectionsService connectionsService,
                             @NonNull FileServerService fileServerService,
                             @NonNull EmailServerService emailServerService,
                             @NonNull SnmpServerService snmpServerService,
                             @NonNull SessionService sessionService,
                             @NonNull AcsLicenseService acsLicenseService,
                             @NonNull AcsConfigService acsConfigService,
                             @NonNull AcsUserService acsUserService,
                             @NonNull AcsWhiteListService whiteListService,
                             @NonNull RetrieveModeService retrieveModeService,
                             @NonNull ProductClassGroupService productClassGroupService,
                             @NonNull DeviceService deviceService,
                             @NonNull ConfigService configService,
                             @NonNull NotificationService notificationService,
                             @NonNull BootstrapService bootstrapService,
                             @NonNull ResourceService resourceService,
                             @NonNull SecurityService securityService,
                             @NonNull HardcodedEventsService hardcodedEventsService) {
        super(alertProvider);
        this.alertsService = alertsService;
        this.interfaceService = interfaceService;
        this.userGroupService = userGroupService;
        this.domainService = domainService;
        this.connectionsService = connectionsService;
        this.fileServerService = fileServerService;
        this.emailServerService = emailServerService;
        this.snmpServerService = snmpServerService;
        this.sessionService = sessionService;
        this.acsLicenseService = acsLicenseService;
        this.acsConfigService = acsConfigService;
        this.acsUserService = acsUserService;
        this.whiteListService = whiteListService;
        this.retrieveModeService = retrieveModeService;
        this.productClassGroupService = productClassGroupService;
        this.deviceService = deviceService;
        this.configService = configService;
        this.notificationService = notificationService;
        this.bootstrapService = bootstrapService;
        this.resourceService = resourceService;
        this.securityService = securityService;
        this.hardcodedEventsService = hardcodedEventsService;
    }

    /**
     * Get all user group
     *
     * @param token authorization from header
     * @return {@link UserResponse} user
     */
    @ApiOperation(value = "Get all user groups")
    @PostMapping("/userGroups")
    public FTPage<UserGroupResponse> getUserGroups(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final UserGroupsBody body) {
        return userGroupService.getUserGroups(token, body);
    }

    @ApiOperation(value = "Get simple user groups (id, name)")
    @PostMapping("/simpleUserGroups")
    public UserGroupsSimpleResponse getSimpleUserGroups(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return userGroupService.getSimpleUserGroups(token);
    }

    /**
     * Get user group by token or id if present
     *
     * @param token authorization from header
     * @return {@link UserResponse} user
     */
    @ApiOperation(value = "Get user group")
    @PostMapping("/userGroup")
    public UserGroupResponse getUserGroup(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                          @RequestBody final UserGroupBody body) {
        return userGroupService.getUserGroup(token, body);
    }

    /**
     * Crete User Group / Update Permissions States
     *
     * @param token authorization from header
     */
    @ApiOperation(value = "Crete User Group / Update Permissions States")
    @PutMapping("/userGroup")
    public UserGroupResponse createOrUpdateUserGroup(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final UserGroupRequest userGroup) {
        return userGroupService.createGroupOrUpdatePermissionStates(token, userGroup);
    }

    /**
     * Delete user group
     *
     * @param token authorization from header
     */
    @ApiOperation(value = "Delete user groups")
    @DeleteMapping("/userGroup")
    public void deleteUserGroup(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                @RequestBody final LongIdsRequest request) {
        userGroupService.deleteUserGroups(token, request.getIds());
    }


    /**
     * Get user group updatedIso
     *
     * @param token authorization from header
     */
    @PostMapping("/userGroup/timestamp")
    public UserGroupTimestamp getUserGroupTimestamp(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token){
        return userGroupService.getUserGroupTimestamp(token);
    }

    /**
     * Get user interface items by token
     *
     * @param token authorization from header
     * @return list of {@link AbstractConfigItem}
     */
    @ApiOperation(value = "Get interface items")
    @PostMapping("/interface")
    public AbstractConfigItemsResponse getInterfaceItems(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return interfaceService.getInterfaceItems(token);
    }

    /**
     * Update user interface item values
     *
     * @param token authorization from header
     */
    @ApiOperation(value = "Update interface items")
    @PutMapping("/interface")
    public AbstractConfigItemsResponse updateInterfaceItems(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                            @RequestBody final AbstractConfigItemsRequest request)
            throws JsonProcessingException {
        return interfaceService.updateInterfaceItems(token, request.getItems());
    }

    @ApiOperation(value = "Check if hide forgot password")
    @PostMapping("/interface/hideForgotPassword")
    public boolean checkHideForgotPassword(@RequestBody ClientType clientType) {
        return interfaceService.getHideForgotPassword(clientType);
    }

    @ApiOperation(value = "Get password reset retry cooldown")
    @PostMapping("/interface/passwordResetRetryCooldown")
    public Integer checkPasswordResetRetryCooldown(@RequestBody ClientType clientType) {
       return interfaceService.getPasswordResetRetryCooldown(clientType);
    }


    @ApiOperation(value = "Get AcsSettings")
    @PostMapping("/ACSSettings")
    public AbstractConfigItemsListResponse getAcsSettings(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return acsConfigService.getAcsConfig(token);
    }

    @ApiOperation(value = "Set AcsSettings")
    @PutMapping("/ACSSettings")
    public void updateAcsSettings(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                  @RequestBody final ConfigItemRequest request) {
        acsConfigService.setAcsConfig(token, request);
    }

    /**
     * Get File Server setting by token
     *
     * @param token authorization from header
     * @return list of {@link ServerDetails}
     */
    @ApiOperation(value = "Get File Server setting")
    @PostMapping("/fileServer")
    public FileServers getFileServerSetting(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                      @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return fileServerService.getFileServerSetting(token);
    }

    /**
     * Update Server Details setting by token
     *
     * @param token authorization from header
     */
    @ApiOperation(value = "Update Server Details setting")
    @PutMapping("/fileServer")
    public void updateFileServerSetting(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                        @RequestBody final ServerDetailsRequest request) {
        fileServerService.updateServerDetailsSetting(token, request.getDetails());
    }

    /**
     * Get Domains setting by token
     *
     * @param token authorization from header
     * @return list of {@link Domain}
     */
    @ApiOperation(value = "Get Domain setting")
    @PostMapping("/domain")
    public Domain getDomain(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                            @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return domainService.getDomain(token);
    }

    /**
     * Create/Update Domain setting by token
     *
     * @param token authorization from header
     * @return {@link Domain}
     */
    @ApiOperation(value = "Add/Update Domain setting")
    @PutMapping("/domain")
    public Domain addOrUpdateDomain(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                    @RequestBody Domain domain) {
        return domainService.createOrUpdateDomain(token, domain);
    }

    /**
     * Delete Domains setting by token
     *
     * @param token authorization from header
     */
    @ApiOperation(value = "Delete Domains setting")
    @DeleteMapping("/domain")
    public void deleteDomains(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                              @RequestBody IntIdsRequest request) {
        domainService.deleteDomains(token, request.getIds());
    }

    /**
     * Get Connections setting by token
     *
     * @param token authorization from header
     * @return {@link Connections}
     */
    @ApiOperation(value = "Get Connections setting")
    @PostMapping("/connections")
    public Connections getConnections(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return connectionsService.getConnections();
    }

    /**
     * Update Connections setting by token
     *
     * @param token authorization from header
     * @return {@link Connections}
     */
    @ApiOperation(value = "Update Connections setting")
    @PutMapping("/connections")
    public void updateAcsAndDb(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                      @RequestBody Connections connections) {
        connectionsService.updateConnections(token, connections);
    }

    /**
     * Get Email Server setting by token
     *
     * @param token authorization from header
     * @return {@link EmailServer}
     */
    @ApiOperation(value = "Get Email Server setting")
    @PostMapping("/emailServer")
    public EmailServers getEmailServer(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return emailServerService.getEmailServer(token);
    }

    /**
     * Update Email Server setting by token
     *
     * @param token authorization from header
     * @return {@link EmailServer}
     */
    @ApiOperation(value = "Update Email Server setting")
    @PutMapping("/emailServer")
    public EmailServer updateEmailServer(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody EmailServer emailServer) {
        return emailServerService.updateEmailServer(token, emailServer);
    }

    /**
     * Get Alerts setting by token
     *
     * @param token authorization from header
     * @return {@link Alerts}
     */
    @ApiOperation(value = "Get Alerts setting")
    @PostMapping("/alerts")
    public AlertsResponse getAlertsSettings(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                    @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return alertsService.getAlertsSetting(token);
    }

    @ApiOperation(value = "Get Alerts events")
    @PostMapping("/alerts/events")
    public AlertEventsResponse getAlertEvents(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                              @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return alertsService.getAlertEvents(token, false);
    }

    /**
     * Update Alerts setting by token
     *
     * @param token authorization from header
     * @return {@link Alerts}
     */
    @ApiOperation(value = "Update Alerts setting")
    @PutMapping("/alerts")
    public Alerts updateAlerts(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody Alerts alerts) {
        return alertsService.updateAlertsSetting(token, alerts);
    }

    /**
     * Get sessions
     *
     * @param token authorization from header
     * @return list of {@link UserSession}
     */
    @ApiOperation(value = "Get active sessions")
    @PostMapping("/sessions")
    public FTPage<UserSession> getActiveSessions(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                 @RequestBody final ActiveSessionsBody body) {
        return sessionService.getActiveSessions(token, body);
    }

    /**
     * Delete sessions
     *
     * @param token authorization from header
     */
    @ApiOperation(value = "Kill sessions")
    @DeleteMapping("/sessions")
    public void killSessions(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                             @RequestHeader(IOT_AUTH_HEADER) final String token,
                             @RequestBody SessionHashesRequest request) {
        sessionService.killSessionsByHash(token, request.getHashes());
    }

    /**
     * Get SNMP Server setting by token
     *
     * @param token authorization from header
     * @return {@link SnmpServer}
     */
    @ApiOperation(value = "Get SNMP Server setting")
    @PostMapping("/snmpServer")
    public SnmpServerResponse getSnmpServer(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                            @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return snmpServerService.getSnmpServer(token);
    }

    /**
     * Update SNMP Server setting by token
     *
     * @param token authorization from header
     * @return {@link SnmpServer}
     */
    @ApiOperation(value = "Update SNMP Server setting")
    @PutMapping("/snmpServer")
    public SnmpServerResponse updateSnmpServer(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                               @RequestBody final SnmpServerRequest emailServer) {
        return snmpServerService.updateSnmpServer(token, emailServer);
    }

    @ApiOperation(value = "Get ACS users")
    @PostMapping("/acsUsers")
    public FTPage<AcsUser> getAcsUsers(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody final AcsUsersBody body) {
        return acsUserService.getAcsUsers(token, body);
    }

    @ApiOperation(value = "Create/Update ACS user")
    @PutMapping("/acsUser")
    public AcsUser updateAcsUser(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                 @RequestBody final AcsUserBody body) {
        return acsUserService.updateAcsUser(token, body);
    }

    @ApiOperation(value = "Delete ACS users")
    @DeleteMapping("/acsUsers")
    public boolean deleteAcsUser(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                 @RequestBody final LoginsRequest request) {
        return acsUserService.deleteAcsUser(token, request.getLogins());
    }

    @PutMapping("/acsUser/csv")
    public void addAcsUsersFromCsv(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @RequestParam final MultipartFile file) {
        acsUserService.addAcsUsers(token, file);
    }

    @ApiOperation(value = "Get license")
    @PostMapping("/license")
    public AcsLicenses getAcsLicense(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                     @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return acsLicenseService.getLicenses(token);
    }

    @ApiOperation(value = "Check license")
    @PostMapping("/licenseCheck")
    public void checkLicense(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                             @RequestHeader(IOT_AUTH_HEADER) final String token) {
        acsLicenseService.checkLicense(token);
    }

    @ApiOperation(value = "Add license")
    @PutMapping("/license")
    public void addAcsLicense(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                              @RequestBody final AddAcsLicense license) {
        acsLicenseService.addLicense(token, license);
    }

    @ApiOperation(value = "Get white list")
    @PostMapping("whiteList")
    public FTPage<AbstractAcsWhiteList> getWhiteList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final WhiteListBody body) {
        return whiteListService.getWhiteList(token, body);
    }

    @ApiOperation(value = "Add white list")
    @PutMapping(value = "whiteList/serialNumber")
    public void addWhiteListSerial(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @RequestParam(required = false) final String description,
                                   @RequestParam(required = false) final String type,
                                   @RequestParam final MultipartFile file) {
        WhiteListRequest request = new WhiteListRequest(description, WhiteListSerialType.valueOf(type));
        whiteListService.addWhiteListSerial(token, request, file);
    }

    @ApiOperation(value = "Add white list")
    @PutMapping(value = "whiteList/ipRange")
    public void addWhiteListIp(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody final WhiteListIpRequest request) {
        whiteListService.addWhiteListIp(token, request);
    }


    @ApiOperation(value = "Remove white list")
    @DeleteMapping("whiteList")
    public void removeWhiteList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                @RequestBody final RemoveWhiteListBody body) {
        whiteListService.removeWhiteList(token, body);
    }

    @ApiOperation(value = "Check serial in white list")
    @PostMapping("whiteList/serial")
    public WhiteListCheckResponse checkWhiteList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                 @RequestBody final AcsCheckWhiteList whiteList) {
        return whiteListService.checkWhiteList(token, whiteList);
    }

    @ApiOperation(value = "Remove serials from white list")
    @DeleteMapping("whiteList/serial")
    public void removeSerialsFromWhiteList(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                           @RequestHeader(IOT_AUTH_HEADER) final String token,
                                           @RequestBody final RemoveSerialFromWhiteList serials) {
        whiteListService.removeSerialFromWhiteList(token, serials);
    }

    @ApiOperation(value = "Get manufacturers")
    @PostMapping("whiteList/manufacturers")
    public ManufacturersResponse getWhiteListManufacturers(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return productClassGroupService.getWhiteListManufacturers(token);
    }

    @ApiOperation(value = "Get unused models")
    @PostMapping("unusedModels")
    public FTPage<UnusedModelsResponse> getUnusedModels(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final RequestPageInfo body) {
        return productClassGroupService.getUnusedModels(token, body);
    }

    @ApiOperation(value = "Delete unused models")
    @DeleteMapping("unusedModels")
    public void deleteUnusedModels(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @RequestBody final LongIdsRequest body) {
        productClassGroupService.deleteUnusedModels(token, body);
    }

    @ApiOperation(value = "Get devices in retrieve mode")
    @PostMapping("retrieveMode")
    public FTPage<RetrieveMode> getRetrieveMode(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                @RequestBody final RetrieveModeBody body) {
        return retrieveModeService.getRetrieveMode(token, body);
    }

    @ApiOperation(value = "On retrieve mode on devices")
    @PutMapping("retrieveMode")
    public boolean addRetrieveMode(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @RequestBody final IntIdsRequest request) {
        return retrieveModeService.addRetrieveMode(token, request.getIds());
    }

    @ApiOperation(value = "Off retrieve mode on devices")
    @DeleteMapping("retrieveMode")
    public boolean removeRetrieveMode(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                      @RequestBody final IntIdsRequest request) {
        return retrieveModeService.removeRetrieveMode(token, request.getIds());
    }

    @ApiOperation(value = "Get manufacturers")
    @PostMapping("retrieveMode/manufacturers")
    public ManufacturersResponse getManufacturers(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return productClassGroupService.getRetrieveManufacturers(token);
    }


    @ApiOperation(value = "Get config tabs")
    @PostMapping("tabs")
    public IotConfigTabsResponse getIotConfigTabs(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final ProtocolTabTypeRequest request) {
        return configService.getIotConfigTabs(token, request.getProtocolType());
    }

    @ApiOperation(value = "Get config properties")
    @PostMapping("/tabs/items")
    public AbstractConfigPropertiesResponse getIotConfigProperties(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                   @RequestBody final IntIdRequest request) {
        return configService.getIotConfigProperties(token, request.getId());
    }

    @ApiOperation(value = "Update config properties")
    @PutMapping("/tabs/items")
    public void getIotConfigProperties(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                       @RequestBody final UpdateIotPropertyRequest request) {
        configService.updateIotConfigProperties(token, request);
    }

    @ApiOperation(value = "Get notification info")
    @PostMapping("/notifications")
    public NotificationInfo getNotificationInfo(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return notificationService.getNotificationInfo(token);
    }

    @ApiOperation(value = "Update notification info")
    @PutMapping("/notifications")
    public NotificationInfo updateNotificationInfo(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody NotificationInfo notificationInfo) {
        return notificationService.updateNotificationInfo(token, notificationInfo);
    }

    @ApiOperation(value = "Get LWM2M security config")
    @PostMapping("lwm2m/security")
    public FTPage<AbstractSecurity> getLWM2MSecurity(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final Lwm2mSecurityBody body) {
        return securityService.getLWM2MSecurityPage(token, body);
    }

    @ApiOperation(value = "Get MQTT security config")
    @PostMapping("mqtt/security")
    public FTPage<AbstractSecurity> getMQTTSecurity(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                    @RequestBody final MqttSecurityBody body) {
        return securityService.getMQTTSecurityPage(token, body);
    }

    @ApiOperation(value = "Get USP security config")
    @PostMapping("usp/security")
    public FTPage<AbstractSecurity> getUSPSecurity(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final UspSecurityBody body) {
        return securityService.getUSPSecurityPage(token, body);
    }

    @ApiOperation(value = "Get security Server Type")
    @PostMapping("lwm2m/security/serverType")
    public ServerTypesResponse getIotSecurityServerTypes(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final ProtocolSecurityTypeRequest request) {
        return securityService.getIotSecurityServerTypes(token, request.getProtocolType());
    }

    @ApiOperation(value = "Get security modes")
    @PostMapping("lwm2m/security/securityMode")
    public SecurityModeTypesResponse getIotSecurityModes(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final SecurityModesBody body) {
        return securityService.getIotSecurityModes(token, body);
    }

    @ApiOperation(value = "Get security mask types")
    @PostMapping("lwm2m/security/maskType")
    public MaskTypesResponse getIotSecurityMaskTypes(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final ProtocolSecurityTypeRequest request) {
        return securityService.getIotSecurityMasks(token, request.getProtocolType());
    }

    @ApiOperation(value = "Add/update security config")
    @PutMapping(value = "lwm2m/security", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public void addIotSecurity(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestParam final String request,
                               @RequestParam(required = false) final MultipartFile certificate) {
        securityService.addSecurity(token, request);
    }

    @ApiOperation(value = "Delete LWM2M security config")
    @DeleteMapping("lwm2m/security")
    public void deleteLWM2MSecurity(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                    @RequestBody final DeleteIotSecurityBody body) {
        securityService.deleteLWM2MSecurity(token, body);
    }


    @ApiOperation(value = "Delete MQTT security config")
    @DeleteMapping("mqtt/security")
    public void deleteMQTTSecurity(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                   @RequestHeader(IOT_AUTH_HEADER) final String token,
                                   @RequestBody final DeleteIotSecurityBody body) {
        securityService.deleteMQTTSecurity(token, body);
    }

    @ApiOperation(value = "Delete USP security config")
    @DeleteMapping("usp/security")
    public boolean deleteUSPSecurity(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                     @RequestBody final DeleteIotSecurityBody body) {
        return securityService.deleteUSPSecurity(token, body);
    }


    @ApiOperation(value = "Get LWM2M security details")
    @PostMapping("lwm2m/security/details")
    public SecurityLWM2MDetails getLWM2MSecurityDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody final SecurityDetailBody body) {
        return securityService.getLWM2MSecurityDetails(token, body);
    }

    @ApiOperation(value = "Get MQTT security details")
    @PostMapping("mqtt/security/details")
    public SecurityMQTTDetails getMQTTSecurityDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                      @RequestBody final SecurityDetailBody body) {
        return securityService.getMQTTSecurityDetails(token, body);
    }

    @ApiOperation(value = "Get USP security details")
    @PostMapping("usp/security/details")
    public SecurityUSPDetails getUSPSecurityDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                    @RequestBody final SecurityDetailBody body) {
        return securityService.getUSPSecurityDetails(token, body);
    }

    @ApiOperation(value = "Check if config detail exists")
    @PostMapping("usp/security/details/identifier/exist")
    public UspIdentifierExists checkIfDetailExists(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final BootstrapConfigDetailsExistBody body) {
        return securityService.checkIfDetailExists(token, body);
    }


    @ApiOperation(value = "Edit LWM2M security details")
    @PutMapping("lwm2m/security/details")
    public SecurityLwm2mEntity getSecurityLWM2MDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                       @RequestBody final SecurityLWM2MDetails body) {
        return securityService.editLWM2MSecurityDetails(token, body);
    }

    @ApiOperation(value = "Edit MQTT security details")
    @PutMapping("mqtt/security/details")
    public SecurityMqttEntity getSecurityMQTTDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final SecurityMQTTDetails body) {
        return securityService.editMQTTSecurityDetails(token, body);
    }

    @ApiOperation(value = "Edit USP security details")
    @PutMapping("usp/security/details")

    public boolean getSecurityUSPDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final SecurityUSPDetailsRequest body) {
        return securityService.editUSPSecurityDetails(token, body);
    }

    @ApiOperation(value = "Get Security Mode by MTP")
    @PostMapping("usp/security/details/mtp")
    public SecurityUspMtpModeResponse getModeByMtp(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return securityService.getAllModeTypesByMTP(token);
    }

    @ApiOperation(value = "Get resource config")
    @PostMapping("lwm2m/resource")
    public FTPage<AbstractResource> getIotResource(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final IotResourceBody body) {
        return resourceService.getIotResourcePage(token, body);
    }

    @ApiOperation(value = "Get details resource config")
    @PostMapping("lwm2m/resource/details")
    public ResourceDetailsResponse getIotResourceDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final IotResourceDetailBody body) {
        return resourceService.getIotResourceDetails(token, body);
    }

    @ApiOperation(value = "Add resource file")
    @PutMapping("lwm2m/resource")
    public TaskIdsResponse addIotResource(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                          @RequestParam final MultipartFile file) {
        return resourceService.addResourceFile(token, file);
    }

    @ApiOperation(value = "Delete resource config")
    @DeleteMapping("lwm2m/resource")
    public void deleteIotResource(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                  @RequestBody DeleteIotResourceBody body) {
        resourceService.deleteIotResource(token, body);
    }

    @ApiOperation(value = "Get bootstrap config")
    @PostMapping("lwm2m/bootstrap/config")
    public FTPage<BootstrapLWM2M> getIotBootstrapConfig(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                        @RequestBody BootstrapConfigBody body) {
        return bootstrapService.getBootstrapConfigPage(token, body);
    }

    @ApiOperation(value = "Get details bootstrap config")
    @PostMapping("lwm2m/bootstrap/config/details")
    public BootstrapLWM2M getIotBootstrapConfigDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                       @RequestBody final BootstrapConfigDetailsBody body) {
        return bootstrapService.getBootstrapConfig(token, body);
    }


    @ApiOperation(value = "Add bootstrap config")
    @PutMapping("lwm2m/bootstrap/config")
    public Integer addIotBootstrapConfig(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                @RequestBody final BootstrapLWM2M config) {
        return bootstrapService.addBootstrapConfig(token, config);
    }

    @ApiOperation(value = "Delete bootstrap config")
    @DeleteMapping("lwm2m/bootstrap/config")
    public void deleteIotBootstrapConfig(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody final IntIdsRequest request) {
        bootstrapService.deleteBootstrapConfig(token, request);
    }

    @ApiOperation(value = "Get bootstrap log")
    @PostMapping("lwm2m/bootstrap/log")
    public FTPage<BootstrapLog> getIotBootstrapLog(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final BootstrapLogBody body) {
        return bootstrapService.getBootstrapLogPage(token, body);
    }

    @ApiOperation(value = "Get bootstrap log details")
    @PostMapping("lwm2m/bootstrap/log/details")
    public BootstrapLogDetailsResponse getIotBootstrapLogDetails(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                 @RequestBody final BootstrapLogDetailsBody body) {
        return bootstrapService.getBootstrapLogDetails(token, body);
    }

    @ApiOperation(value = "Delete bootstrap log")
    @DeleteMapping("lwm2m/bootstrap/log")
    public void deleteIotBootstrapLog(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                      @RequestBody final DeleteBootstrapConfigBody body) {
        bootstrapService.deleteBootstrapLog(token, body);
    }

    @ApiOperation(value = "Delete all bootstraps log by param")
    @DeleteMapping("lwm2m/bootstrap/log/all")
    public void deleteAllIotBootstrapLog(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                         @RequestHeader(IOT_AUTH_HEADER) final String token,
                                         @RequestBody final DeleteAllBootstrapLogBody body) {
        bootstrapService.deleteAllBootstrapLog(token, body);
    }

    @ApiOperation(value = "Update permission config")
    @PutMapping(value = "/userGroup/permissions", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public boolean updatePermissionConfig(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                          @RequestParam final MultipartFile config) {
        return userGroupService.updatePermissionConfig(token, config);
    }


    @ApiOperation(value = "Get hardcoded events list")
    @PostMapping("/events/hardcoded")
    public HardcodedEventsResponse getEvents(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                             @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return hardcodedEventsService.getEvents(token);
    }

    @ApiOperation(value = "Update hardcoded events")
    @PutMapping(value = "/events/hardcoded")
    public void eventUpdate(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                            @RequestBody final HardcodedEventRequest request) {
        hardcodedEventsService.changeEvent(token, request);
    }

    @ApiOperation(value = "Get hardcoded events urls list")
    @PostMapping("/events/hardcoded/urls")
    public HardcodedEventsUrlsResponse getEventUrls(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return hardcodedEventsService.getEventUrls(token);
    }

    @ApiOperation(value = "Update hardcode events urls")
    @PutMapping(value = "/events/hardcoded/urls")
    public void eventsUrlUpdate(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                @RequestBody final HardcodedEventUrlRequest request) {
        hardcodedEventsService.changeUrls(token, request);
    }

    @ApiOperation(value = "Remove hardcode events urls")
    @DeleteMapping("/events/hardcoded/urls")
    public void eventUrlRemove(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                               @RequestBody final HardcodedEventUrlDeleteRequest request) {
        hardcodedEventsService.removeUrls(token, request);
    }

    @ApiOperation(value = "Check if user has these user group")
    @PostMapping("/userGroup/users/exist")
    public CheckUserGroupResponse checkIfUserGroupExists(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                         @RequestBody final CheckUserGroupRequest body) {
        return userGroupService.checkUserGroupExists(token, body);
    }

    @ApiOperation(value = "Check if dependencies exists")
    @PostMapping("/domain/dependencies/exist")
    public CheckDependencyResponse checkDependenciesExists(@ApiParam(value = IOT_AUTH_HEADER, required = true,
            hidden = true) @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                           @RequestBody final CheckDependencyRequest body) {
        return userGroupService.checkDependencyExist(token, body);
    }

    @ApiOperation(value = "Get ACS nodes info")
    @PostMapping("/nodes")
    public NodesListResponse getAcsNodesInfo(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                              @RequestHeader(IOT_AUTH_HEADER) final String token) {
        return alertsService.getAcsNodesInfo(token);
    }

}