package com.friendly.commons.exceptions;

import com.friendly.commons.errors.model.FriendlyError;

/**
 * Defines base Exception for all Exceptions in Friendly Tech
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class BaseFriendlyException extends RuntimeException {

    private final int code;

    public BaseFriendlyException(final String message, final int code) {
        super(message);
        this.code = code;
    }

    public BaseFriendlyException(final FriendlyError error, final Object... args) {
        super(error.getFormattedMessage(args));
        this.code = error.getErrorCode();
    }

    public BaseFriendlyException(final String message, final Exception innerException, final int code) {
        super(message, innerException);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
