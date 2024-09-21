package com.friendly.services.uiservices.user.orm.iotw.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.friendly.commons.models.Themes;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.UserGroupSimpleEntity;
import com.friendly.services.uiservices.system.orm.iotw.model.LocaleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Model that represents persistence version of User
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_user")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AbstractEntity<Long> {

    @Column(name = "client_type", nullable = false, updatable = false)
    private ClientType clientType;

    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "time_format")
    private String timeFormat;

    @Column(name = "user_group_id")
    private Long userGroupId;

    @Column(name = "locale_id")
    private String localeId;

    @Column(name = "domain_id")
    private Integer domainId;

    @Column(name = "blocked")
    private Boolean blocked;

    @Column(name = "is_change_password")
    private Boolean isChangePassword;

    @Column(name = "last_change_password")
    private Instant lastChangePassword;

    @Column(name = "failed_attempts")
    private Integer failedAttempts;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "expire_date")
    private Instant expireDate;

    @Column(name = "theme_name")
    @Enumerated(EnumType.STRING)
    private Themes themeName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserGroupSimpleEntity userGroup;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locale_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LocaleEntity locale;

}
