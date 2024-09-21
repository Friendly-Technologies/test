package com.friendly.services.settings.bootstrap.orm.acs.model;

import com.friendly.commons.models.settings.security.IdentifierType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iot_security_configuration")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUspEntity extends AbstractEntity<Integer> {

    @Column(name = "active")
    private Boolean active;

    @Column(name = "location_id")
    private Integer domainId;

    @Column(name = "identifier_name")
    private String identifier;

    @Column(name = "identifier_type")
    @Enumerated(EnumType.STRING)
    private IdentifierType identifierType;

    @Column(name = "description")
    private String description;

    @Column(name = "protocol_id")
    private Integer protocolId;

    @ToString.Exclude
    @OneToMany(mappedBy = "security", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("id asc")
    private List<SecurityUspMtpEntity> securityUspMtpEntities;

}
