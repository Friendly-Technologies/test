package com.friendly.services.settings.userinterface.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_interface_domain_specific")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceSpecificDomain extends AbstractEntity<Long> {

    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    @Column(name = "domain_id", nullable = false)
    private Integer domainId;

    @Column(name = "value")
    private String value;

    @Column(name = "interface_id")
    private String interfaceItemId;


}
