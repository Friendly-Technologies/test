package com.friendly.commons.models.settings.bootstrap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapResource implements Serializable {
    private String name;
    private String value;
    private InstanceType instanceType;
    private Integer objectId;
    private List<BootstrapResource> resources;
}
