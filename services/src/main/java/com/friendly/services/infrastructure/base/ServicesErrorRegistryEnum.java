package com.friendly.services.infrastructure.base;

import com.friendly.commons.errors.model.FriendlyError;

/**
 * Defines the error codes for Services validation.
 * All Services errors have codes from 9000 to 9999.
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public enum ServicesErrorRegistryEnum implements FriendlyError {

    USER_IS_NOT_FOUND(
            9000,
            "User with id: '%s' is not found",
            true),

    USER_GROUP_NOT_FOUND(
            9002,
            "User group is not found"),

    ADMIN_GROUP_CAN_NOT_DELETED(
            9003,
            "Can not be deleted Admin Group"),

    CAN_NOT_SERIALIZE_OBJECT(
            9004,
            "Can not deserialization %s",
            true),

    CAN_NOT_ADD_DOMAIN(
            9005,
            "Can not add domain"),

    CAN_NOT_UPDATE_DOMAIN(
            9006,
            "Can not update domain"),

    CAN_NOT_DELETE_DOMAIN(
            9007,
            "Can not delete domains"),

    REPORT_IS_EMPTY(
            9008,
            "Report is empty"),

    UNABLE_TO_MAKE_CONNECTION(
            9009,
            "Unable to make connection with '%s'",
            true),

    WRONG_CONFIGURATION(
            9010,
            "Unable to set configuration"),

    INCORRECT_LOGIN_OR_PASSWORD(
            9011,
            "Incorrect login or password to '%s'",
            true),

    DATABASE_NOT_FOUND(
            9012,
            "Database '%s' not found",
            true),

    DOMAIN_NAME_IS_NOT_UNIQUE(
            9013,
            "Domain with name '%s' already exist",
            true),

    DOMAIN_NOT_FOUND(
            9014,
            "Domain with name '%s' not found",
            true),

    USER_NOT_UNIQUE(
            9015,
            "User with this username already exist",
            false),

    USER_NOT_FOUND(
            9016,
            "User with username '%s' not found",
            true),

    WRONG_PASSWORD(
            9017,
            "Invalid username or password"),

    DENIED_DOMAIN(
            9018,
            "User Domain denied"),

    ILLEGAL_CHARACTER(
            9019,
            "Illegal character in '%s'",
            true),

    VIEW_NOT_FOUND(
            9020,
            "View with id %s not found",
            true),

    INVALID_TOKEN(
            9021,
            "Token is expired or invalid"),

    VIEW_NOT_EXIST(
            9022,
            "The previously selected view is no longer available or removed."),

    CAN_NOT_DELETE_FILE(
            9023,
            "Can not delete file '%s'",
            true),

    DEVICE_NOT_FOUND(
            9024,
            "Device was not found"),

    FILE_FORMAT_NOT_SUPPORTED(
            9025,
            "Format not supported"),

    CAN_NOT_DELETE_DEVICE(
            9026,
            "Can not delete device"),

    CAN_NOT_UPDATE_ACCOUNT_INFO(
            9027,
            "Can not update account info"),

    CAN_NOT_DOWNLOAD_FILE(
            9028,
            "Can not download file"),

    CAN_NOT_UPLOAD_FILE(
            9029,
            "Can not upload file"),

    CAN_NOT_ADD_DIAGNOSTIC(
            9030,
            "Can not add diagnostic"),

    DIAGNOSTIC_NOT_FOUND(
            9031,
            "Diagnostic with id '%s' not found",
            true),

    PROVISION_NOT_FOUND(
            9032,
            "Provision with id '%s' not found",
            true),

    OPERATION_NOT_SUPPORTED(
            9033,
            "Operation '%s' not supported",
            true),

    MONITORING_NOT_FOUND(
            9034,
            "Monitoring not found"),

    DEVICE_PARAMETER_NOT_FOUND(
            9035,
            "Device parameter '%s' not found",
            true),

    CAN_NOT_GET_PARAMETERS(
            9036,
            "Can not get current device parameters"),

    FRAME_NOT_FOUND(
            9037,
            "Frame with id '%s' not found",
            true),

    MESSAGE_NOT_SUPPORTED(
            9038,
            "Message not supported: '%s' ",
            true),

    CAN_NOT_CONNECT_TO_FTP(
            9039,
            "Can't connect to upload FTP server"),

    CAN_NOT_START_TRACE(
            9040,
            "Can not start tracing device"),

    CAN_NOT_STOP_TRACE(
            9041,
            "Can not stop tracing device"),

    FIELD_CAN_NOT_BE_EMPTY(
            9042,
            "Field '%s' can not be empty",
            true),

    CAN_NOT_ADD_OBJECT(
            9043,
            "Can not add device object: '%s'",
            true),

    CAN_NOT_DELETE_OBJECT(
            9044,
            "Can not delete device object: '%s'",
            true),

    CAN_NOT_START_PING(
            9045,
            "Can not start ping device"),

    CAN_NOT_ADD_DEVICE(
            9046,
            "Can not add device"),

    NO_PERMISSION(
            9047,
            "No permission to '%s'",
            true),

    DIAGNOSTIC_TYPE_NOT_SUPPORTED(
            9048,
            "Diagnostic type '%s' not supported",
            true),

    FRAME_NOT_UNIQUE(
            9049,
            "Frame with name '%s' already exist",
            true),

    USER_GROUP_NOT_UNIQUE(
            9050,
            "User group with name '%s' already exist",
            true),

    SESSION_NOT_FOUND(
            9051,
            "Session not found"),

    ACS_EXCEPTION(
            9052,
            "ACS Exception: %s",
            true),

    ACS_EXCEPTION_USER_READABLE(
            9052,
            "Looks like something went wrong.\nAdding information to log",
            true),

    LICENSE_NOT_UNIQUE(
            9053,
            "License Id violated"),

    WHITE_LIST_NOT_SUPPORTED(
            9054,
            "White List type not supported"),

    PROPERTY_NOT_FOUND(
            9055,
            "IoT configuration property with id '%s' not found",
            true),

    CAN_NOT_OVERRIDE_PROPERTY(
            9056,
            "IoT configuration property '%s' not overridable",
            true),

    PARAMETER_NOT_UNIQUE(
            9057,
            "'%s' must be unique",
            true),

    TAB_NOT_FOUND(
            9058,
            "Tab '%s' not found",
            true),

    CAN_NOT_DESERIALIZE_TIME(
            9059,
            "Can not deserialize time"),

    PERMISSION_NOT_FOUND(
            9060,
            "The user was blocked by administrator",
            true),

    FAILED_ATTEMPTS(9061,
            "Too many attempts, account is temporary blocked"),

    FILE_FTP_NOT_FOUND(
            9058,
            "File ftp entity '%s' not found",
            true),

    USERNAME_AND_EMAIL_NOT_FOUND(
            9062,
            "User with username '%s' and email '%s' not found",
            true),

    RESTORE_PASSWORD_FAILED(
            9063,
            "Sorry, password recovery is failed, something went wrong. Please, try again later"),

    RESET_PASSWORD_EXPIRED(
            9064,
            "The URL is no longer valid. " +
                    "Please retry the Forgot Password procedure " +
                    "or contact an administrator at 'phone number' for assistance"),

    USER_BLOCKED_TIME_EXPIRED(
            9065,
            "The user was blocked due to time expiration"),
    DIAGNOSTIC_DETAILS_NOT_FOUND(
            9066,
            "Diagnostic details for diagnostic type '%s' not found",
            true),
    TASK_NAME_NOT_FOUND(
            9067,
            "Task name cannot be null"),
    UNKNOWN_DB_TYPE(
            9068,
            "Unknown DB type"),

    EXCEL_STRATEGY_NOT_FOUND(
            9069,
            "Excel report generation strategy not found for report type: '%s'",
            true),
    DEVICE_PROFILE_NOT_FOUND(9070, "Device profile not found", false),
    NOT_SUPPORTED_TYPE(
            9071,
            "Not supported type: %s",
            true),
    DATE_IS_NULL(
            9072,
            "The date cannot be null"),

    CLIENT_TYPES_ARE_NOT_COMPATIBLE(
            9073,
            "Client types are not compatible"),
    PRODUCT_CLASS_GROUP_NOT_FOUND(9074,
            "group not found by manufacturer: %s and model: %s"),
    UNKNOWN_AUTH_TYPE(
            9075,
            "Unknown Auth type: %s",
            true),
    UNKNOWN_INTERFACE_ITEM(
            9075,
            "Unknown Interface item: %s",
            true),
    UNSUPPORTED_AUTH_METHOD(
            9075,
                    "Unsupported authentication method"),
    AUTH_IS_NULL(
            9076,
                    "The Authentication cannot be null"),
    AUTH_PARSING_FAILED(
            9077,
            "Authentication configuration parsing failed"),
    VALUE_NOT_FOUND(
            9078,
            "Value not found for: %s",
            true),
    PASSWORD_EXPIRED(
            9079,
            "Password is expired"),
    EMAIL_ERROR(
            9080,
            "Email server error during attempt to send email"),
    INTERFACE_VALUE_NOT_FOUND(
            9081,
            "Interface value %s not found"),
    VERSION_READ_ERROR(
            9082,
            "Error reading version from version.xml: %s",
            true),
    INIT_LICENSE_UTILS_ERROR(
            9083,
            "Exception to initialize LicenseUtils"),
    DECRYPT_ERROR(
            9084,
            "Can't decrypt"),
    ENCRYPT_ERROR(
            9085,
            "Can't encrypt"),
    VERSION_FILE_NOT_FOUND(
            9082,
            "Not found file version.xml"),
    COLUMN_KEY_NOT_FOUND(
            9083,
            "Not found column key: %s",
            true),
    CAN_NOT_CONNECT_TO_HTTP(
            9084,
            "Can't connect to HTTP: %s",
            true),
    UPDATE_GROUP_NOT_FOUND(
            9085,
            "Update Group with id %s not found",
            true);



    private final int errorCode;
    private final String errorMessage;
    private final boolean isMessageParameterized;

    /**
     * @param errorCode    Error Code associated with the error
     * @param errorMessage Error Message associated with the error
     */
    ServicesErrorRegistryEnum(final int errorCode, final String errorMessage) {

        this(errorCode, errorMessage, false);
    }

    /**
     * @param errorCode              Error Code associated with the error
     * @param errorMessage           Error Message associated with the error
     * @param isMessageParameterized Indicates that Error Message requires additional parameters for formatted output
     */
    ServicesErrorRegistryEnum(
            final int errorCode,
            final String errorMessage,
            final boolean isMessageParameterized) {

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.isMessageParameterized = isMessageParameterized;
    }

    /**
     * Gets Code of the error
     *
     * @return Unique Error Code
     **/
    @Override
    public int getErrorCode() {

        return errorCode;
    }

    /**
     * Gets Message associated with the error
     *
     * @return Error Message
     */
    @Override
    public String getErrorMessage() {

        return errorMessage;
    }

    /**
     * Checks if error message requires additional parameters
     *
     * @return True if error message requires additional parameters before formatting, otherwise False
     */
    @Override
    public boolean isMessageParameterized() {

        return isMessageParameterized;
    }

    /**
     * Returns formatted error message with additional information about concrete case
     *
     * @param args List of arguments used for formatting
     * @return Formatted error message
     */
    @Override
    public String getFormattedMessage(final Object... args) {
        return String.format(getErrorMessage(), args);
    }
}