package com.friendly.commons.errors;

import com.friendly.commons.errors.model.FriendlyError;

/**
 * Defines the error codes for Common System Exceptions.
 * All Common System Exceptions errors have codes from 1 to 999.
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public enum SystemErrorRegistry implements FriendlyError {

    /**
     * An unrecognized error occurred
     */
    MESSAGING_SYSTEM_ERROR(
            1,
            "Unrecognized System Exception handled by Messaging System",
            true),

    /**
     * Cannot apply json patch to the entity
     */
    JSON_PATCH_CANNOT_BE_APPLIED(
            2,
            "Cannot apply a patch to the entity. The reason: %s\nThe patch: %s"
    ),

    /**
     * Cannot restore entity after patching
     */
    JSON_PATCH_CANNOT_BE_RESTORED(
            3,
            "Cannot restore the entity of type '%s' after applying the patch. The reason: %s"
    ),

    /**
     * Received file is unsupported
     */
    UNABLE_TO_PARSE_FILE(
            5,
            "Received file with name '%s' and type '%s' is unsupported. Error message: '%s'",
            true),

    /**
     * Received file cannot be parsed
     */
    UNSUPPORTED_FILE_TYPE(
            6,
            "Received file with name '%s' cannot be parsed. Error: '%s', message: '%s'",
            true),

    /**
     * Can't parse value into an int
     */
    INTEGER_NOT_PARSABLE(
            7,
            "Value %s can't be parsed into an int",
            true),

    /**
     * Page value not valid
     */
    PAGE_VALUE_NOT_IN_RANGE(
            8,
            "Page value of %s is not valid",
            true),

    /**
     * Generic error for when the error code is unknown
     */
    UNKNOWN_ERROR_CODE(
            9,
            "Unknown error code"),

    /**
     * Field is not supported
     */
    FIELD_NOT_SUPPORTED(
            10,
            "Field %s not supported",
            true),

    /**
     * Limit value not valid
     */
    LIMIT_VALUE_NOT_IN_RANGE(
            11,
            "Limit value of %s is not valid",
            true),

    /**
     * Values out of range
     */
    VALUES_OUT_OF_RANGE(
            12,
            "Values out of range %s",
            true),

    /**
     * Combination of page and page size is out of range
     */
    PAGE_AND_PAGE_SIZE_OUT_OF_RANGE(
            12,
            "Combination of page %s and page size %s out of range",
            true),

    /**
     * Both the start and end query params are required to use slice
     */
    START_AND_END_NOT_PROVIDED(
            13,
            "Both start and end query parameters are required to use slice feature",
            false);

    private final int errorCode;
    private final String errorMessage;
    private final boolean isMessageParameterized;

    /**
     * @param errorCode    Error Code associated with the error
     * @param errorMessage Error Message associated with the error
     */
    SystemErrorRegistry(final int errorCode, final String errorMessage) {

        this(errorCode, errorMessage, false);
    }

    /**
     * @param errorCode              Error Code associated with the error
     * @param errorMessage           Error Message associated with the error
     * @param isMessageParameterized Indicates that Error Message requires additional parameters for formatted output
     */
    SystemErrorRegistry(
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
