package com.friendly.commons.models.settings.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListParameter implements Serializable {

    private List<String> urls;
    private Boolean isDefault;
    private List<Parameter> parameters;

}
