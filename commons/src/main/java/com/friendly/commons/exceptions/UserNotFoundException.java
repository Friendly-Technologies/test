package com.friendly.commons.exceptions;

import com.friendly.commons.errors.model.FriendlyError;

/**
 * Indicates that keycloak user wasn't found.
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public class UserNotFoundException extends BaseFriendlyException {

    public UserNotFoundException(final FriendlyError error,
                                 final Object... args) {
        super(error, args);
    }
}
