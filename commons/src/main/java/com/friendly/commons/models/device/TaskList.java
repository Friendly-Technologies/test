package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class TaskList implements Serializable {

    private Integer completedTasks;
    private Integer failedTasks;
    private Integer pendingTasks;
    private Integer rejectedTasks;
    private Integer sentTasks;
}
