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
public class ParamDetails implements Serializable {

    private Long id;
    private Integer index;
    private List<String> fullNames;
    private InputType inputType;
    private DataFormatType dataFormatType;

    //textbox, password
    private Boolean required;

    //text, integer
    private String blackList;
    private String whiteList;

    //integer
    private String scale;
    private Integer minValue;
    private Integer maxValue;

    //radio, select
    private List<ParamOption> options;

}
