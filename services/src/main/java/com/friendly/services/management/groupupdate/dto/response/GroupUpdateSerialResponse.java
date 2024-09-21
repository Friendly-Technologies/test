package com.friendly.services.management.groupupdate.dto.response;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateDeviceStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateSerialResponse {
    private Long id;
    private String serial;
    GroupUpdateDeviceStateType state;
    Boolean selected;
}
