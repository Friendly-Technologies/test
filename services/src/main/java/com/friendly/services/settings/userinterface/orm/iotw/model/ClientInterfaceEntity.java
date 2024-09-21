package com.friendly.services.settings.userinterface.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Model that represents persistence version of Interface Item
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "iotw",name = "iotw_client_interface")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClientInterfaceEntity extends AbstractEntity<Long> {

    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    @Column(name = "value", length = 2048)
    private String value;

    @Column(name = "is_encrypted")
    private Boolean isEncrypted;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "interface_id", referencedColumnName = "id")
    private InterfaceItemEntity interfaceItem;

    private String domainSpecificValue;

}
