package com.friendly.services.uiservices.system.orm.iotw.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Model that represents persistence version of Server Details Entity
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_server_details")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ServerDetailsEntity extends AbstractEntity<Long> {

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "server_details_id")
    private Long serverDetailsId;

}
