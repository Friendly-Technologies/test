package com.friendly.services.uiservices.system.orm.iotw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model that represents persistence version of Locale
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Entity
@Table(name = "iotw_locale")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocaleEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "value", nullable = false)
    private String value;
}
