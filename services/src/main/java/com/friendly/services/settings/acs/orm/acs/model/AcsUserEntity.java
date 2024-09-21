package com.friendly.services.settings.acs.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Entity
@Table(name = "cpe_login")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcsUserEntity implements Serializable {

    @Id
    @Column(name = "login")
    private String login;

    @Column(name = "location_id")
    private Integer domainId;

    @Column(name = "password")
    private String password;

}
