package com.friendly.commons.models.user;

import com.friendly.commons.models.FieldSort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Model that represents API version of User controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AllUsersBody implements Serializable {
	private List<Integer> pageNumbers;
    private Integer pageSize;
    private String searchParam;
    private Boolean searchExact;
    private List<FieldSort> sorts;
            
}
