package com.friendly.integration;

/**
 * Defines base Exception with Code
 *
 * @author aleksandr.kaygorodov
 * @since 0.0.2
 */
public class ClientException extends RuntimeException {

    private final int code;

    ClientException(final ClientErrorRegistry error, final Object... args) {
        this(error.getFormattedMessage(args), error.getErrorCode());
    }

    public ClientException(final String message, final int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
