package com.friendly.services.device.info.orm.acs.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Entity
@Table(name = "cust_device1")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomDeviceEntity implements Serializable {

    @Id
    @Column(name = "serial")
    private String serial;

    @Column(name = "telephone")
    private String phone;

    @Column(name = "login_name")
    private String userLogin;

    @Column(name = "name")
    private String userName;

    @Column(name = "zip")
    private String zip;

    @Column(name = "location")
    private String userLocation;

    @Column(name = "user_tag")
    private String userTag;

    @Column(name = "userstatus")
    private String userStatus;

    @Column(name = "userid")
    private String userId;

    @Column(name = "cust1")
    private String cust1;

    @Column(name = "cust2")
    private String cust2;

    @Column(name = "cust3")
    private String cust3;

    @Column(name = "cust4")
    private String cust4;

    @Column(name = "cust5")
    private String cust5;

    @Column(name = "cust6")
    private String cust6;

    @Column(name = "cust7")
    private String cust7;

    @Column(name = "cust8")
    private String cust8;

    @Column(name = "cust9")
    private String cust9;

    @Column(name = "cust10")
    private String cust10;

    @Column(name = "cust11")
    private String cust11;

    @Column(name = "cust12")
    private String cust12;

    @Column(name = "cust13")
    private String cust13;

    @Column(name = "cust14")
    private String cust14;

    @Column(name = "cust15")
    private String cust15;

    @Column(name = "cust16")
    private String cust16;

    @Column(name = "cust17")
    private String cust17;

    @Column(name = "cust18")
    private String cust18;

    @Column(name = "cust19")
    private String cust19;

    @Column(name = "cust20")
    private String cust20;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

}
