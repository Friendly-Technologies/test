package com.friendly.services.management.groupupdate.dto.response;

import com.friendly.services.management.groupupdate.dto.GroupUpdateTask;
import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateDeviceStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateDeviceReport implements Serializable {

    private Long id;
    private String serial;
    private String manufacturer;
    private String model;
    private String activated;
    private Instant activatedIso;
    private GroupUpdateDeviceStateType state;
    private Map<String, GroupUpdateTask> tasks;

}
