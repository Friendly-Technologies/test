package com.friendly.services.management.groupupdate.dto.request;

import com.friendly.services.management.groupupdate.dto.base.AbstractGroupUpdateGroupDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
public class GroupUpdateGroupDetailsRequest extends AbstractGroupUpdateGroupDetails {
    List<GroupUpdateGroupChildDetailsModify> configs;
}