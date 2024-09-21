package com.friendly.services.management.action.dto.response;

import com.friendly.commons.models.device.file.FileActType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadTaskActionResponse extends AbstractActionResponse {
    private Integer delay;
    private String description;
    private Integer fileTypeId;
    private FileActType actType;
    private String link;
    private String fileName;
    private Boolean isManual = false;
    private String password;
    private String url;
    private String username;
    private Integer instance;
}
