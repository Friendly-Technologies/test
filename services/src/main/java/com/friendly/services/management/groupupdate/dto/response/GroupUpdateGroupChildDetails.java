package com.friendly.services.management.groupupdate.dto.response;

import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.groupupdate.dto.base.AbstractGroupUpdateGroupChildDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateGroupChildDetails extends AbstractGroupUpdateGroupChildDetails {
    List<ActionListResponse> tasks;
}