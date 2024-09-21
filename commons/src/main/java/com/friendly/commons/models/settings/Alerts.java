package com.friendly.commons.models.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * Model that represents persistence version of Alerts Notification Setting
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Alerts implements Serializable {

    private boolean viaProgram;
    private boolean viaEmail;
    private boolean viaSms;
    private boolean viaSnmp;

    private Set<String> emails;
    private Set<String> phoneNumbers;

    private AlertTimesType alertTimesType;
    private Long interval;

}
