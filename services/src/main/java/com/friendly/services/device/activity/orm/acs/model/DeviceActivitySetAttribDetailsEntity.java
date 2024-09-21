package com.friendly.services.device.activity.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DeviceActivitySetAttribDetailsEntity implements Serializable {
    @Id
    private String name;
    private Integer notification;
    private String accessList;
    private String creator;
}
