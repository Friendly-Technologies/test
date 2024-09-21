package com.friendly.commons.models.view.response;

import com.friendly.commons.models.device.DeviceColumns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class DeviceColumnsResponse {
    List<DeviceColumns> items;
}
