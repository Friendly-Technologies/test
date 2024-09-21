package com.friendly.web.client;

/**
 * Exception thrown by the REST API Client when an entity is not found,
 * producing a HTTP Status of 404.
 *
 * @author aleksandr.kaygorodov
 * @since 0.0.2
 */
public class NotFoundException extends ClientException {

    public NotFoundException(final String message, final int code) {
        super(message, code);
    }

    public NotFoundException(final ClientErrorRegistry error, final Object... args) {
        super(error, args);
    }
}
