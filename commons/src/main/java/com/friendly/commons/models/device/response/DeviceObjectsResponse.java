package com.friendly.commons.models.device.response;

import com.friendly.commons.models.device.setting.DeviceObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class DeviceObjectsResponse {
    List<DeviceObject> items;
}
