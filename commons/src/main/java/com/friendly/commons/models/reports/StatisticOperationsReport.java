package com.friendly.commons.models.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Model that represents persistence version of ACS Address
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StatisticOperationsReport implements Serializable {

    private Integer maxValue;
    private List<OperationsReport> operations;

}
