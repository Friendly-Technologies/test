package com.friendly.services.management.groupupdate.utils.strategy;

import com.friendly.services.management.groupupdate.dto.GroupUpdateFilters;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateSerialResponse;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.UpdateGroupProjection;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeviceGroupUpdateStrategy {
    List<GroupUpdateSerialResponse> getDevices(GroupUpdateFilters filters, Pageable pageable, UpdateGroupProjection projection);
}
