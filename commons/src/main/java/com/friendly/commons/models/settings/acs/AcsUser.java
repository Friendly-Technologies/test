package com.friendly.commons.models.settings.acs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcsUser implements Serializable {

    private String login;
    private String password;
}
