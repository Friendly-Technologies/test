package com.friendly.commons.exceptions;

import com.friendly.commons.errors.model.FriendlyError;
import com.friendly.commons.errors.model.ValidationError;

public class FriendlyUnauthorizedException extends BaseFriendlyException {

    public FriendlyUnauthorizedException(final ValidationError error) {
        super(error.getErrorMessage(), error.getCode());
    }

    public FriendlyUnauthorizedException(final String message, final int code) {
        super(message, code);
    }

    public FriendlyUnauthorizedException(final FriendlyError error, final Object... args) {
        super(error.getFormattedMessage(args), error.getErrorCode());
    }

    public FriendlyUnauthorizedException(final String message, final Exception innerException, final int code) {
        super(message, innerException, code);
    }
}

