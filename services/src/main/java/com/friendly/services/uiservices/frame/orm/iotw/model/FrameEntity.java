package com.friendly.services.uiservices.frame.orm.iotw.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Model that represents persistence version of Column Name
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_view_frame")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FrameEntity extends AbstractEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column
    private String icon;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "size_height")
    private Integer size;

    @Column(name = "property_type")
    private String propertyType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "frame")
    @ToString.Exclude
    private List<FrameTitleEntity> titles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "frame")
    @ToString.Exclude
    private List<FrameParamEntity> rows;
}
