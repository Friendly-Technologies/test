package com.friendly.commons.models.device.diagnostics;

import com.friendly.commons.models.device.TaskStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that represents API version of Device
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDiagnostics implements Serializable {

    private Long id;
    private String created;
    private String completed;
    private Instant createdIso;
    private Instant completedIso;
    private TaskStateType state;
    private String diagnosticsTypeKey;
    private String diagnosticsTypeName;

}
