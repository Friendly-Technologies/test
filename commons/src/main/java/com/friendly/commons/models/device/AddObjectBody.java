package com.friendly.commons.models.device;

import com.friendly.commons.models.device.setting.AddObjectRequest;
import com.friendly.commons.models.device.setting.DeviceAddObjectRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AddObjectBody implements Serializable {
    private Long deviceId;
    private AddObjectRequest request;

}
