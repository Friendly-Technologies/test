package com.friendly.commons.exceptions;

import com.friendly.commons.errors.model.FriendlyError;
import com.friendly.commons.errors.model.ValidationError;

/**
 * Defines error occurs if entity with given Id was not found
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class FriendlyEntityNotFoundException extends BaseFriendlyException {

    public FriendlyEntityNotFoundException(final ValidationError error) {
        super(error.getErrorMessage(), error.getCode());
    }

    public FriendlyEntityNotFoundException(final String message, final int code) {
        super(message, code);
    }

    public FriendlyEntityNotFoundException(final FriendlyError error, final Object... args) {
        super(error.getFormattedMessage(args), error.getErrorCode());
    }

    public FriendlyEntityNotFoundException(final String message, final Exception innerException, final int code) {
        super(message, innerException, code);
    }

}
