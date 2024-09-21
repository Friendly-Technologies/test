package com.friendly.services.management.action.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskCondition implements Serializable {
    private String condition;
    private String name;
    private String value;
}
