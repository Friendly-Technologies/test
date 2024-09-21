package com.friendly.commons.models.settings.config.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIotProperty implements Serializable {

    private Long id;
    private String value;

}
