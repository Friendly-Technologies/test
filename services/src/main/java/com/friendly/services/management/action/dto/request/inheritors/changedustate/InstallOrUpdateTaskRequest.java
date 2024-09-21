package com.friendly.services.management.action.dto.request.inheritors.changedustate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstallOrUpdateTaskRequest implements Serializable {
    private String uuid;
    private String url;
    private String username;
    private String password;
    private String link;
    private String fileName;
}
