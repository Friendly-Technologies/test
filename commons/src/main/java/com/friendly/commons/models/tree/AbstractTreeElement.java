package com.friendly.commons.models.tree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractTreeElement implements Serializable {
    private String fullName;
    private String shortName;
}
