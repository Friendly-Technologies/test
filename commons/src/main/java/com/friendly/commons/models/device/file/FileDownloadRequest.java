package com.friendly.commons.models.device.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Model that represents API version of File Upload Fields
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadRequest extends AbstractFileRequest {

    private Boolean resetSession;
    private Integer fileSize;

    private String fileContent;
    private String targetFileName;
    private String fileVersion;
    private Boolean sendBytes;

    private String failureURL;
    private String successURL;

    private DeliveryProtocolType deliveryProtocol;
    private DeliveryMethodType deliveryMethod;

    private Boolean newest;
}
