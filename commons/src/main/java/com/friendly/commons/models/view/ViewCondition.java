package com.friendly.commons.models.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

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
public class ViewCondition implements Serializable {

    private Long id;
    private String columnKey;
    private String columnName;
    private ConditionLogic logic;
    private ConditionType compare;
    private String compareName;
    private String conditionString;
    private Instant conditionDateIso;
    private String conditionDate;

    private List<ViewCondition> items;

}
