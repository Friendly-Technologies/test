package com.friendly.commons.models.settings;

import com.friendly.commons.models.settings.acs.AcsUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Settings controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcsUserBody implements Serializable {
    private String login;
    private String password;
 
}