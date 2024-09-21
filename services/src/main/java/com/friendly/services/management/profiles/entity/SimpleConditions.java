package com.friendly.services.management.profiles.entity;

import com.friendly.commons.models.device.software.SoftwareUnInstallRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SimpleConditions implements Serializable {

    private List<SimpleCondition> items;
}
