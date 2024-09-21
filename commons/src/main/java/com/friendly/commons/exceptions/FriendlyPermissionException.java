package com.friendly.commons.exceptions;

import com.friendly.commons.errors.model.FriendlyError;
import com.friendly.commons.errors.model.ValidationError;

/**
 * Defines error occurs if user not have permissions
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class FriendlyPermissionException extends BaseFriendlyException {

    public FriendlyPermissionException(final ValidationError error) {
        super(error.getErrorMessage(), error.getCode());
    }

    public FriendlyPermissionException(final String message, final int code) {
        super(message, code);
    }

    public FriendlyPermissionException(final FriendlyError error, final Object... args) {
        super(error.getFormattedMessage(args), error.getErrorCode());
    }

    public FriendlyPermissionException(final String message, final Exception innerException, final int code) {
        super(message, innerException, code);
    }
}
