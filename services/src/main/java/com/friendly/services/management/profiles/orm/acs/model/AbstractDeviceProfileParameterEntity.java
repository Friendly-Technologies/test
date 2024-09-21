package com.friendly.services.management.profiles.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDeviceProfileParameterEntity implements Serializable {
    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "name")
    private String name;

    @Column(name = "profile_id")
    private Integer profileId;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updator")
    private String updator;
}
