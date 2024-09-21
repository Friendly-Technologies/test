package com.friendly.services.management.profiles.entity;

import com.friendly.commons.models.view.ConditionLogic;
import com.friendly.commons.models.view.ConditionType;
import com.friendly.services.management.profiles.ConditionGroupType;
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
public class GroupConditionFilterItem implements Serializable {

    private Long id;
    private String columnKey;
    private String columnName;
    private ConditionType compare;
    private String compareName;
    private String conditionString;
    private ConditionGroupType type;

}
