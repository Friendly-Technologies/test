package com.friendly.services.device.parameterstree.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractParameterEntity implements Serializable {

    @Transient
    private String parentName;

    @Transient
    private String shortName;

    @Transient
    private String fullName;

    @Transient
    private List<AbstractParameterEntity> parameters;

    public abstract Long getCpeId();
    public abstract Long getNameId();
    public abstract String getValue();
    public abstract Boolean getWriteable();

    @Transient
    public Long getId() {return 0L;}
}
