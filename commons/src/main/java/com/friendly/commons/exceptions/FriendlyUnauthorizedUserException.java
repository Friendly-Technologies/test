package com.friendly.commons.exceptions;

import com.friendly.commons.errors.model.FriendlyError;
import com.friendly.commons.errors.model.ValidationError;

/**
 * Defines error occurs if user not authorized
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class FriendlyUnauthorizedUserException extends BaseFriendlyException {

    public FriendlyUnauthorizedUserException(final ValidationError error) {
        super(error.getErrorMessage(), error.getCode());
    }

    public FriendlyUnauthorizedUserException(final String message, final int code) {
        super(message, code);
    }

    public FriendlyUnauthorizedUserException(final FriendlyError error, final Object... args) {
        super(error.getFormattedMessage(args), error.getErrorCode());
    }

    public FriendlyUnauthorizedUserException(final String message, final Exception innerException, final int code) {
        super(message, innerException, code);
    }
}
