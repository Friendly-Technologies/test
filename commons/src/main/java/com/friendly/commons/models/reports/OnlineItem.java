package com.friendly.commons.models.reports;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OnlineItem implements Serializable {

    private String domain;
    private String manufacturer;
    private String model;
    private Integer quantity;

    @JsonIgnore
    private Date lastSession;

}
