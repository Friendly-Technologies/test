package com.friendly.commons.models.user;

import com.friendly.commons.models.Themes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * Model that defines a User
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String dateFormat;
    private String timeFormat;
    private Instant expireDateIso;
    private Boolean blocked;
    private Boolean isChangePassword;
    @NotNull
    private Long userGroupId;
    @NotNull
    private String localeId;
    private Integer domainId;
    private Themes themeName;
}
