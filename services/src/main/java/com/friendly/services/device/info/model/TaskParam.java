package com.friendly.services.device.info.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskParam implements Serializable {

    private String name;
    private String value;
    private String creator;
    private Integer errorCode;
    private String errorText;
}
