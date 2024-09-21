package com.friendly.commons.models.device.request;

import com.friendly.commons.models.device.DeviceConfigType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceConfigTypeRequest {
    DeviceConfigType type;
}
