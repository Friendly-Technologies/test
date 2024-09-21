package com.friendly.services.management.action.dto.request.inheritors;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import com.friendly.commons.models.device.file.FileDownloadRequest;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadTaskAction extends AbstractActionRequest {
    private FileDownloadRequest fileDownloadRequests;
}
