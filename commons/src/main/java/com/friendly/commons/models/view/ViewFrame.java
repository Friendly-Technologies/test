package com.friendly.commons.models.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class ViewFrame implements Serializable {

    private Long id;
    private String name;
    private Boolean isDefault;
    private String icon;
    private Integer index;
    private Integer size;
    private PropertyType type;
    private String parameterName;

    private List<FrameTitle> titles;
    private List<FrameRow> rows;

}
