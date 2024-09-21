package com.friendly.commons.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * Model that represents persistence version of Domain
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Domain implements Serializable {

    private Integer id;
    private String name;
    private String fullName;
    private Set<Domain> items;

}
