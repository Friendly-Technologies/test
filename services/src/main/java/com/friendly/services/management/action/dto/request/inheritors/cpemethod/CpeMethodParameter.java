package com.friendly.services.management.action.dto.request.inheritors.cpemethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class CpeMethodParameter implements Serializable {
    private String method;
    private String value;
    @JsonIgnore // should be added later, logic for instance ready but need check
    private String instance;
}
