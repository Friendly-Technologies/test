package com.friendly.services.device.info.model;

import com.friendly.commons.models.device.TaskStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskSimple implements Serializable {

    private TaskStateType state;
    private Instant completed;
}
