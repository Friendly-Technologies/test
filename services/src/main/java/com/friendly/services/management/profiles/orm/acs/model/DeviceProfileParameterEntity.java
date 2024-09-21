package com.friendly.services.management.profiles.orm.acs.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Table(name = "profile_parameter")
@Entity
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProfileParameterEntity extends AbstractDeviceProfileParameterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "value")
    private String value;
}
