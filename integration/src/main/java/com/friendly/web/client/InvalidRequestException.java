package com.friendly.web.client;

import com.friendly.commons.errors.model.ValidationError;
import com.google.common.base.Joiner;

import java.util.List;


/**
 * Exception thrown by the REST API Client when invalid request received,
 * producing a HTTP Status of 400.
 *
 * @author aleksandr.kaygorodov
 * @since 0.0.2
 */
public class InvalidRequestException extends ClientException {

    InvalidRequestException(final String message, final int code){
        super(message, code);
    }

    public InvalidRequestException(final ClientErrorRegistry error, final Object... args) {
        super(error, args);
    }

    public InvalidRequestException(final List<ValidationError> errors) {
        super(
                String.format(
                        "The following validation errors occur: %s",
                        Joiner.on("\r\n\t")
                              .join(errors)),
                errors.get(0) != null ? errors.get(0).getCode() : null);
    }
}
