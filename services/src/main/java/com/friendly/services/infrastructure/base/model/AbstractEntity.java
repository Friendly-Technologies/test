package com.friendly.services.infrastructure.base.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.TypeDef;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Base class for all persistence entities. Contains data and logic shared between all persistence entities
 *
 * @param <I> The type of the ID for the particular entity
 * @author Friendly Tech
 * @since 0.0.2
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class AbstractEntity<I extends Number> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private I id;
}
