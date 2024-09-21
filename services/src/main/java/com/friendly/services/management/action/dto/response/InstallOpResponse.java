package com.friendly.services.management.action.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class InstallOpResponse extends AbstractActionResponse implements Serializable {
    private String url;
    private String username;
    private String password;
    private String uuid;
    private Boolean isManual;
    private String fileName;
    private String link;
}
