package com.friendly.commons.models.settings.bootstrap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that represents API version of Device Activity
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapLogDetail implements Serializable {

    private Integer id;
    private Instant createdIso;
    private String created;
    private String description;
    private String activityType;
    private String sender;
    private String request;
    private String response;
    private String status;
}
