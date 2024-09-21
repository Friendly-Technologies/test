package com.friendly.commons.models.settings.acs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IotConfigTab implements Serializable {

    private Integer id;
    private String name;
    private String fullName;
    private String description;

    private List<IotConfigTab> items;

}
