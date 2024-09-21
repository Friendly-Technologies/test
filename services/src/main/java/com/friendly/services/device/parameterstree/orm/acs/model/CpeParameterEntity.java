package com.friendly.services.device.parameterstree.orm.acs.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cpe_parameter" )
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CpeParameterEntity extends AbstractParameterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(name = "cpe_id" )
    private Long cpeId;

    @Column(name = "name_id")
    private Long nameId;

    @Column(name = "value")
    private String value;

    @Column(name = "writeable")
    private Boolean writeable;

    @Column(name = "updated")
    private Instant updated;
}
