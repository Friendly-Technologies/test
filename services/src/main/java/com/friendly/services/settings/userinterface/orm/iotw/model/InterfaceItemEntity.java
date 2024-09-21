package com.friendly.services.settings.userinterface.orm.iotw.model;

import com.friendly.commons.models.settings.config.ValueType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that represents persistence version of Interface Item
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Entity
@Table(name = "iotw_interface_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceItemEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "default_value", length = 2048)
    private String defaultValue;
    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "value_type", nullable = false)
    private ValueType valueType;

    @Column(name = "group_id")
    private String groupId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "interface_description_id", referencedColumnName = "id")
    private List<InterfaceDescriptionEntity> interfaceDescriptions;

    private String domainSpecificValue;

}
