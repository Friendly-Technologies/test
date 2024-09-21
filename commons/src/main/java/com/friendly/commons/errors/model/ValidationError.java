package com.friendly.commons.errors.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Defines validation error
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {

    private int code;
    private boolean isWarning;
    private String errorMessage;

    public ValidationError(final int code, final String errorMessage) {
        this(code, false, errorMessage);
    }

    @Override
    public String toString() {
        return String.format(
                "%s%d: %s",
                isWarning ? "[WARNING]" : "",
                code,
                errorMessage);
    }
}
