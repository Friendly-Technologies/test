package com.friendly.commons.models.view;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that represents API version of View Frame
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ViewFrameRequest implements Serializable {

    private Long id;
    private Integer index;
    private PropertyType type;

}
