package com.friendly.services.management.action.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UpdateOpResponse extends AbstractActionResponse implements Serializable {
    private String url;
    private String username;
    private String password;
    private String link;
    private String fileName;
    private String uuid;
    private Boolean isManual;
}
