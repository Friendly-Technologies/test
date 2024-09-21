package com.friendly.commons.models.device.file;

import com.friendly.commons.models.device.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Device History
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FileTypeFilter implements Serializable {

    private Integer id;
    private String name;
    private Boolean canInstance;
    private Boolean canRestore;
    private ProtocolType protocolType;
}
