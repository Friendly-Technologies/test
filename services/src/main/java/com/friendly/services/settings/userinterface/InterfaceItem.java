package com.friendly.services.settings.userinterface;

public enum InterfaceItem {

    AUTHENTICATION_TYPE("AuthenticationType"),
    AUTO_START_IP_PING_DIAGNOSTICS("AutoStartIpPingDiagnostics"),
    CONNECT_FAILURE_LIMIT("ConnectFailureLimit"),
    DEFAULT_USER_LANGUAGE("DefaultUserLanguage"),
    DEVICE_STATUS_RATE("DeviceStatusRate"),
    DEVICE_STATUS_TIMEOUT("DeviceStatusTimeout"),
    DISABLE_USER_PERIOD_ON_ATTEMPTS_EXCEED("DisableUserPeriodOnAttemptsExceed"),
    DOMAINS_ENABLE("DomainsEnable"),
    DOWNLOAD_URL_FOR_SPEED_TEST("DownloadUrlForSpeedTest"),
    FRAME_IGNORE_OFFLINE("FrameIgnoreOffline"),
    HIDE_WAN_PASSWORD("HideWanPassword"),
    ISP_ENABLE("IspEnable"),
    ITEMS_PAGE_SIZE_LIST("ItemsPageSizeList"),
    ITEMS_PER_PAGE("ItemsPerPage"),
    MAX_FAILED_LOGIN("MaxFailedLogin"),
    NETWORK_MAP_WITHOUT_NAME("NetworkMapWithoutName"),
    NS_LOOKUP_DIAGNOSTIC("NsLookupDiagnostic"),
    ONLINE_STATE_TIMEOUT("OnlineStateTimeout"),
    PARAMETERS_FOR_MONITORING("ParametersForMonitoring"),
    PING_FROM_DEVICE("PingFromDevice"),
    REBOOT_AMOUNT_LIMIT("RebootAmountLimit"),
    REMOTE_URL("RemoteURL"),
    SHOW_PASSWORD_AS_PLAIN_TEXT("ShowPasswordAsPlainText"),
    TRACE_ROUTE_DIAGNOSTIC("TraceRouteDiagnostic"),
    UPLOAD_DIAGNOSTICS_FILE_SIZE("UploadDiagnosticsFileSize"),
    UPLOAD_EXTENSIONS("UploadExtensions"),
    UPLOAD_URL_FOR_SPEED_TEST("UploadUrlForSpeedTest"),
    USED_CHANNELS_2_4GHZ("UsedChannels2.4GHz"),
    USED_CHANNELS_5GHZ("UsedChannels5GHz"),
    USED_CHANNELS_6GHZ("UsedChannels6GHz"),
    PASSWORD_DAYS_VALID("PasswordDaysValid"),
    HIDE_FORGOT_PASSWORD("HideForgotPassword"),
    PASSWORD_RESET_RETRY_COOLDOWN ("PasswordResetRetryCooldown");

    private final String value;

    InterfaceItem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

