package com.friendly.commons.models.device;

import com.friendly.commons.models.device.setting.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Model that represents API version of Device Activity
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceActivity implements Serializable {

    private Long taskId;
    private TaskStateType taskState;
    private String taskName;
    private List<Parameter> parameters;
    private String created;
    private Instant createdIso;
    private String completed;
    private Instant completedIso;
    private String application;
    private String creator;
    private Integer errorCode;
    private String errorText;
}
