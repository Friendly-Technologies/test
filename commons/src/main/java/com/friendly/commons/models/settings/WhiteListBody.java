package com.friendly.commons.models.settings;

import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.settings.acs.whitelist.WhiteListType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Model that represents API version of Settings controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WhiteListBody implements Serializable {
	private WhiteListType type;
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private List<FieldSort> sorts;
    
}
