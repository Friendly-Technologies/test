package com.friendly.commons.errors.model;

/**
 * Declares interface for all error registries used in Friendly Tech
 * Contains basis operation to get information about an error.
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public interface FriendlyError {

    /**
     * Gets Code of the error
     *
     * @return Unique Error Code
     **/
    int getErrorCode();

    /**
     * Gets Message associated with the error
     *
     * @return Error Message
     */
    String getErrorMessage();

    /**
     * Checks if error message requires additional parameters
     *
     * @return True if error message requires additional parameters before formatting, otherwise False
     */
    boolean isMessageParameterized();

    /**
     * Returns formatted error message with additional information about concrete case
     *
     * @param args List of arguments used for formatting
     * @return Formatted error message
     */
    String getFormattedMessage(Object... args);
}
