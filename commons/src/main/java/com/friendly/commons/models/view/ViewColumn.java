package com.friendly.commons.models.view;

import com.friendly.commons.models.OrderDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of View Columns and Orders
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ViewColumn implements Serializable {

    private String columnKey;
    private String columnName;
    private Boolean canSort;

    private Integer indexVisible;

    private Integer indexSort;
    private OrderDirection direction;

}
