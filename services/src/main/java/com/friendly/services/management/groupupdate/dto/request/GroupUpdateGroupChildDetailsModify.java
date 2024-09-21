package com.friendly.services.management.groupupdate.dto.request;

import com.friendly.services.management.action.dto.request.AbstractActionRequest;
import com.friendly.services.management.groupupdate.dto.base.AbstractGroupUpdateGroupChildDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class GroupUpdateGroupChildDetailsModify extends AbstractGroupUpdateGroupChildDetails {
    List<Integer> selectedDevices;
    List<Integer> unselectedDevices;
    List<AbstractActionRequest> tasks;
}