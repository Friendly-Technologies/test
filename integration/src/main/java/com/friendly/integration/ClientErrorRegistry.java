package com.friendly.integration;

/**
 * Defines the error codes for Client library.
 * All ClientErrorRegistry errors have codes from 100 to 199.
 *
 * @author aleksandr.kaygorodov
 * @since 0.0.2
 */
public enum ClientErrorRegistry {

    /**
     * Empty error collection.
     */
    CLIENT_EMPTY_ERROR_COLLECTION(
            100,
            "Empty error collection"),

    /**
     * Unsupported status code.
     */
    CLIENT_UNSUPPORTED_STATUS_CODE(
            101,
            "Unsupported status code: %s",
            true),

    /**
     * Error executing collection request
     */
    CLIENT_ERROR_EXECUTING_COLLECTION_REQUEST(
            102,
            "Error executing collection request"),

    /**
     * Service instance was not found
     */
    CLIENT_SERVICE_INSTANCE_WAS_NOT_FOUND(
            103,
            "Service instance of %s for zone %s was not found",
            true),

    /**
     * Internal Jackson error
     */
    CLIENT_JACKSON_ERROR(
            104,
            "Exception message: %s",
            true),

    /**
     * Internal HTTP REST error
     */
    CLIENT_REST_ERROR(
            105,
            "Exception message: %s",
            true),

    /**
     * Response is empty.
     */
    CLIENT_EMPTY_RESPONSE(
            106,
            "Response is empty."
    );

    private final int errorCode;
    private final String errorMessage;
    private final boolean isMessageParameterized;

    /**
     * @param errorCode    Error Code associated with the error
     * @param errorMessage Error Message associated with the error
     */
    ClientErrorRegistry(final int errorCode, final String errorMessage) {
        this(errorCode, errorMessage, false);
    }

    /**
     * @param errorCode              Error Code associated with the error
     * @param errorMessage           Error Message associated with the error
     * @param isMessageParameterized Indicates that Error Message requires additional parameters for formatted output
     */
    ClientErrorRegistry(
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
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Gets Message associated with the error
     *
     * @return Error Message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Checks if error message requires additional parameters
     *
     * @return True if error message requires additional parameters before formatting, otherwise False
     */
    public boolean isMessageParameterized() {
        return isMessageParameterized;
    }

    /**
     * Returns formatted error message with additional information about concrete case
     *
     * @param args List of arguments used for formatting
     * @return Formatted error message
     */
    public String getFormattedMessage(final Object... args) {
        return String.format(getErrorMessage(), args);
    }
}
