package com.friendly.commons.models.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class FileExistResponse {
    private Boolean exist;
}
