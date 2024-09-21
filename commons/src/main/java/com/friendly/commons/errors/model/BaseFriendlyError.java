package com.friendly.commons.errors.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * {@link FriendlyError} base class with reusable common properties and methods
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@RequiredArgsConstructor
@Getter
public class BaseFriendlyError implements FriendlyError {

    private final int errorCode;
    private final String errorMessage;
    private final boolean isMessageParameterized;

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