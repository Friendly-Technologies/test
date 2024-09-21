package com.friendly.commons.models.device.file;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Model that represents API version of File Request
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = AbstractFileRequest.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = FileActType.class,
        visible = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "DOWNLOAD", value = FileDownloadRequest.class),
        @JsonSubTypes.Type(name = "RESTORE", value = FileDownloadRequest.class),
        @JsonSubTypes.Type(name = "UPLOAD", value = FileUploadRequest.class),
        @JsonSubTypes.Type(name = "BACKUP", value = FileUploadRequest.class)
})
public abstract class AbstractFileRequest implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "actType";

    private FileActType actType;
    private Integer fileTypeId;
    private String url;
    private String username;
    private String password;
    private Integer delay;
    private Boolean push;
    private Boolean reprovision;
    private String description;
    private Boolean isManual = false;
    private String link;
    private String fileName;

}
