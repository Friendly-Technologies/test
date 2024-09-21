package com.friendly.commons.models.view;

import lombok.Getter;

@Getter
public class GetDeviceViewsListRequest {
    private ViewType viewType;
    private Long deviceId;
}
