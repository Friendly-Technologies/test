package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that represents API version of Device
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Device implements Serializable {

    private Long id;

    //CPE
    private String created;
    private Integer status;
    private String serial;
    private String updated;
    private String firmware;
    private ProtocolType protocolType;

    private Instant createdIso;
    private Instant updatedIso;

    private String domainName;

    private String manufacturer;
    private String model;
    private String oui;

    //Cust_device1

    private String userLogin;
    private String userName;
    private String phone;
    private String zip;
    private String userLocation;
    private String userTag;
    private String userStatus;
    private String userId;
    private String cust1;
    private String cust2;
    private String cust3;
    private String cust4;
    private String cust5;
    private String cust6;
    private String cust7;
    private String cust8;
    private String cust9;
    private String cust10;

    private Integer completedTasks;
    private Integer failedTasks;
    private Integer pendingTasks;
    private Integer rejectedTasks;
    private Integer sentTasks;

    private String hardware;
    private String software;
    private String ipAddress;
    private String macAddress;
    private String uptime;
    private String acsUsername;

}
