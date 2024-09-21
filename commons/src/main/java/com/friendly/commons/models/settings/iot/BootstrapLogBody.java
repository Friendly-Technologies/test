package com.friendly.commons.models.settings.iot;

import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.settings.bootstrap.ProtocolBootstrapType;
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
public class BootstrapLogBody implements Serializable {
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private List<FieldSort> sorts;
    private String searchParam;
    private boolean searchExact;
}
