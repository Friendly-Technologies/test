package com.friendly.services.management.profiles.orm.acs.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Table(name = "profile_parameter_access")
@Entity
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceProfileParameterAccessEntity extends AbstractDeviceProfileParameterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "access_list")
    private String accessList;
}
