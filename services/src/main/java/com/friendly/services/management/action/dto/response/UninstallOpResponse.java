package com.friendly.services.management.action.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UninstallOpResponse extends AbstractActionResponse implements Serializable {
    private String name;
    private String uuid;
    private String version;
}
