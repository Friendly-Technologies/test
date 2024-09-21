package com.friendly.commons.models.device;

import lombok.Getter;

@Getter
public class WifiAautoRescanBody {
    private String autChannelParamName;
    private String channelParamName;
    private Integer currentChannel;
    private Long deviceId;

}
