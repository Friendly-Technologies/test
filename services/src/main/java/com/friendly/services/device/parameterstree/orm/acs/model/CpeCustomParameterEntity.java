package com.friendly.services.device.parameterstree.orm.acs.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_custom_parameter")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CpeCustomParameterEntity extends AbstractParameterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(name = "cpe_id")
    private Long cpeId;

    @Column(name = "name_id")
    private Long nameId;

    @Column(name = "value")
    private String value;

    @Override
    public Boolean getWriteable() {
        return false;
    }
}
