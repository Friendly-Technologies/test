package com.friendly.commons.models.settings.iot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class UspSecurityBody implements Serializable {
    private Integer domainId;
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private String searchParam;
    private Boolean searchExact;

}
