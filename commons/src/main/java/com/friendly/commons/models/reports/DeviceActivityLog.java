package com.friendly.commons.models.reports;

import com.friendly.commons.models.auth.ClientType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceActivityLog implements Serializable {

    Long userId;
    ClientType clientType;
    DeviceActivityType activityType;
    Long deviceId;
    String serial;
    String note;
    Long groupId;

}
