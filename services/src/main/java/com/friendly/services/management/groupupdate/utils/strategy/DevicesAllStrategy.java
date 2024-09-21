package com.friendly.services.management.groupupdate.utils.strategy;

import com.friendly.services.management.groupupdate.dto.GroupUpdateFilters;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateSerialResponse;
import com.friendly.services.management.groupupdate.service.GroupUpdateService;
import com.friendly.services.management.groupupdate.orm.acs.model.projection.UpdateGroupProjection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevicesAllStrategy implements DeviceGroupUpdateStrategy {
    @NonNull
    private GroupUpdateService groupUpdateService;

    @Override
    public List<GroupUpdateSerialResponse> getDevices(GroupUpdateFilters filters, Pageable pageable, UpdateGroupProjection projection) {
        return groupUpdateService.getDevicesAll(filters, pageable);
    }
}
