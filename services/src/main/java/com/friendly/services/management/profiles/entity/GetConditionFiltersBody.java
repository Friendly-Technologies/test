package com.friendly.services.management.profiles.entity;

import com.friendly.services.management.profiles.ConditionGroupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of IOT controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GetConditionFiltersBody implements Serializable {
	private String manufacturer;
    private String model;
    private ConditionGroupType conditionType;
}
