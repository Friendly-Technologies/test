package com.friendly.services.management.groupupdate.dto;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateActivationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateActivation implements Serializable {
    GroupUpdateActivationType type;
    private String date;
    List<GroupUpdateActivationPeriod> periodList;
    Integer threshold;
    Boolean requestDeviceConnect;
    Boolean onlyOnlineDevices;
    Boolean stopOnFail;
}