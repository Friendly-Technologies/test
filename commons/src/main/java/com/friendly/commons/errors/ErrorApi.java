package com.friendly.commons.errors;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that represents Error message or REST API layer
 *
 * @author Friendly Tech
 * @implNote error_description is used to map keycloak service exceptions to instance of {@link ErrorApi}
 * @since 0.0.2
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorApi {

    public static final int UNKNOWN_ERROR_CODE = -1;

    @JsonAlias({"error", "error_description", "description"})
    private String error;

    @JsonAlias("code")
    private int code;

    public static ErrorApiBuilder builder(final String error, final int code) {

        return new ErrorApiBuilder().error(error)
                                    .code(code);
    }
}
