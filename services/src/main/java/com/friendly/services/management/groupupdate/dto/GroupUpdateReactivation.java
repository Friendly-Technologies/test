package com.friendly.services.management.groupupdate.dto;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateReactivationEndsEnum;
import com.friendly.services.management.groupupdate.dto.enums.TimeIntervalEnum;
import com.friendly.services.management.groupupdate.dto.enums.WeekDaysEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateReactivation implements Serializable {
    TimeIntervalEnum type;
    Integer repeatEvery;
    List<WeekDaysEnum> repeatOn;
    String startsOn;
    GroupUpdateReactivationEndsEnum ends;
    Integer reactivationCount;
    String endsReactivationDate;
    Boolean reRunFailedDevices;
}