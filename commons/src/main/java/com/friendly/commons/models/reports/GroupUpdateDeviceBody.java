package com.friendly.commons.models.reports;

import com.friendly.commons.models.FieldSort;
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
public class GroupUpdateDeviceBody implements Serializable {
	private Integer id;
    private Long groupId;
    private String serial;
    private Boolean searchExact;
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private List<FieldSort> sorts;
    
    
    
}
