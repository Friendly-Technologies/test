package com.friendly.services.management.groupupdate.utils.strategy;

import com.friendly.services.management.groupupdate.dto.GroupUpdateFilters;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateSerialResponse;
import com.friendly.services.management.groupupdate.service.GroupUpdateService;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.UpdateGroupProjection;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceCompletedUgGroupUpdateStrategy implements DeviceGroupUpdateStrategy {

    @NonNull
    private GroupUpdateService groupUpdateService;
    
    @Override
    public List<GroupUpdateSerialResponse> getDevices(GroupUpdateFilters filters, Pageable pageable, UpdateGroupProjection projection) {
        return groupUpdateService.getDevicesCompletedUG(filters, pageable, projection);
    }
}
