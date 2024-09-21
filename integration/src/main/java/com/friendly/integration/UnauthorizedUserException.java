package com.friendly.integration;

/**
 * Exception thrown by the REST API Client when an entity is not found,
 * producing a HTTP Status of 404.
 *
 * @author aleksandr.kaygorodov
 * @since 0.0.2
 */
public class UnauthorizedUserException extends ClientException {

    public UnauthorizedUserException(final String message, final int code) {
        super(message, code);
    }

    public UnauthorizedUserException(final ClientErrorRegistry error, final Object... args) {
        super(error, args);
    }
}
