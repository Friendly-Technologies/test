package com.friendly.commons.models.tree;

import com.friendly.commons.models.tree.AbstractTreeElement;
import com.friendly.commons.models.tree.AbstractTreeParameterValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
public class TreeParameter extends AbstractTreeElement {

}
