package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.friendly.commons.models.settings.security.MaskType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lwm2m_bs")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BootstrapLwm2mEntity extends AbstractEntity<Integer> {

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updator")
    private String updater;

    @Column(name = "name")
    private String name;

    @Column(name = "endpoint_mask")
    private String mask;

    @Column(name = "endpoint_mask_type")
    @Enumerated(EnumType.STRING)
    private MaskType maskType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "bootstrap")
    @ToString.Exclude
    @JsonIgnore
    @OrderBy("id asc")
    private List<BootstrapSecurityLwm2mEntity> securities;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "bootstrap")
    @ToString.Exclude
    @JsonIgnore
    @OrderBy("id asc")
    private List<BootstrapServerLwm2mEntity> servers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "bootstrap")
    @ToString.Exclude
    @JsonIgnore
    @OrderBy("id asc")
    private List<BootstrapResourceLwm2mEntity> resources;

}
