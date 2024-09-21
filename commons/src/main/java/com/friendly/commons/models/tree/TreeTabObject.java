package com.friendly.commons.models.tree;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class TreeTabObject extends TreeObject<TreeTabObject, TreeTabParameter> {

}
