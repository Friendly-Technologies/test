package com.friendly.commons.models.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that represents persistence version of ACS Address
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceReport implements Serializable {

    private String userName;
    private String manufacturer;
    private String model;
    private String serial;
    private String domain;
    private String date;
    private Instant dateIso;
    private DeviceActivityType activityType;
    private String activityName;
    private String note;

}
