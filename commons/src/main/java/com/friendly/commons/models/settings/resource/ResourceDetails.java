package com.friendly.commons.models.settings.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDetails implements Serializable {

    private Integer id;
    private Integer objectId;
    private String name;
    private String path;
    private String operations;
    private Boolean mandatory;
    private ResourceType instanceType;
    private String valueRange;
    private ResourceValueType valueType;
    private String units;
    private String description;
    private String value;
    private Integer instanceId;
    private List<ResourceDetails> items;
    private List<ResourceDetails> parameters;

}