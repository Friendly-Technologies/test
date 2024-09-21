package com.friendly.commons.models.settings.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Manufacturer implements Serializable {

    private Long id;
    private String manufacturer;
    private String model;
    private String url;
    private Boolean canDelete;
}
