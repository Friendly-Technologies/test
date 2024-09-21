package com.friendly.commons.exceptions;

import com.friendly.commons.errors.model.FriendlyError;
import com.friendly.commons.errors.SystemErrorRegistry;
import com.friendly.commons.errors.model.ValidationError;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Defines error occurs when invalid argument is passed
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class FriendlyIllegalArgumentException extends BaseFriendlyException {

    final static Logger LOG = LoggerFactory.getLogger(FriendlyIllegalArgumentException.class);

    public FriendlyIllegalArgumentException(final List<ValidationError> errors) {
        super(String.format(
                "The following validation errors occur: %s",
                Joiner.on("\r\n\t")
                      .join(errors)), getErrorCode(errors));
    }

    public FriendlyIllegalArgumentException(final String message, final int code) {
        super(message, code);
    }

    public FriendlyIllegalArgumentException(final ValidationError error) {
        super(error.getErrorMessage(), error.getCode());
    }

    public FriendlyIllegalArgumentException(final FriendlyError error, final Object... args) {
        super(error.getFormattedMessage(args), error.getErrorCode());
    }

    public FriendlyIllegalArgumentException(final Exception innerException, final FriendlyError error, final Object... args) {
        super(error.getFormattedMessage(args), innerException, error.getErrorCode());
    }

    public FriendlyIllegalArgumentException(final String message, final Exception innerException, final int code) {
        super(message, innerException, code);
    }

    private static int getErrorCode(final List<ValidationError> errors) {
        if (errors == null || errors.size() <= 0 || errors.get(0) == null) {
            LOG.warn("Unable to retrieve validation error code");
            return SystemErrorRegistry.UNKNOWN_ERROR_CODE.getErrorCode();
        }

        return errors.get(0).getCode();
    }
}
