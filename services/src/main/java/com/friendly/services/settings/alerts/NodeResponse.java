package com.friendly.services.settings.alerts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NodeResponse {
    private String name;
    private String nodeName;
    private String state;
}
