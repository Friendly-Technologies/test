package com.friendly.integration;

/**
 * Exception thrown by the REST API Client when internal server error occurs,
 * producing a HTTP Status of 500.
 *
 * @author aleksandr.kaygorodov
 * @since 0.0.2
 */

public class InternalServerException extends ClientException {

    public InternalServerException(final ClientErrorRegistry error, final Object... args) {
        super(error, args);
    }

    InternalServerException(final String message, final int code) {
        super(message, code);
    }

}
