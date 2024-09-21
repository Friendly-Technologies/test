package com.friendly.services.management.groupupdate.orm.acs.model.projection;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateStateType;

public interface UpdateGroupChildProjection {
    Long getId();

    Long getGroupId();

    GroupUpdateStateType getState();
}
