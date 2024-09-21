package com.friendly.services.management.groupupdate.dto.response;

import com.friendly.services.management.groupupdate.dto.base.AbstractGroupUpdateGroupDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateGroupDetailsResponse extends AbstractGroupUpdateGroupDetails {
    List<GroupUpdateGroupChildDetails> configs;
}