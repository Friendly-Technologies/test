package com.friendly.commons.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.friendly.commons.models.Themes;
import com.friendly.commons.models.auth.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class UserResponse implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String dateFormat;
    private String timeFormat;
    private String expireDate;
    private Instant expireDateIso;
    private UserStatusType status;
    private DomainSimple domain;
    private Locale locale;
    private UserGroupSimple userGroup;
    private Boolean blocked;
    private Instant lastLogin;
    private Integer failedAttempts;
    private Themes themeName;
    private Boolean isChangePassword;

    @JsonIgnore
    private ClientType clientType;
    @JsonIgnore
    private Long userGroupId;
    @JsonIgnore
    private String localeId;
    @JsonIgnore
    private Integer domainId;

}
