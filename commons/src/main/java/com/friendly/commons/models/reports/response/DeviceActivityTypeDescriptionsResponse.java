package com.friendly.commons.models.reports.response;

import com.friendly.commons.models.reports.DeviceActivityTypeDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceActivityTypeDescriptionsResponse {
    List<DeviceActivityTypeDescription> items;
}
