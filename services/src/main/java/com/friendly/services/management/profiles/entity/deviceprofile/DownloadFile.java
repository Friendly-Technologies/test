package com.friendly.services.management.profiles.entity.deviceprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadFile {
    private Integer fileTypeId;
    private String url;
    private String created;
    private String creator;
}
