package com.friendly.commons.models.settings;

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
public class RemoveWhiteListBody implements Serializable {
	private WhiteListType type;
    private List<Integer> ids; 
}
