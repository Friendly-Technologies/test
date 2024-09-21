package com.friendly.services.settings.bootstrap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Lwm2mInstanceEntity {
    private Integer instanceId;
    private Integer resourceInstanceId;
    private String value;
    private String name;
}
