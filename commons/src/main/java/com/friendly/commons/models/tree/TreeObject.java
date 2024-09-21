package com.friendly.commons.models.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
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
@AllArgsConstructor
public class TreeObject<O extends TreeObject<O, P>, P extends  TreeParameter> extends AbstractTreeElement {
    private List<O> items;
    private List<P> parameters;
    @JsonIgnore
    private O parent;
}
