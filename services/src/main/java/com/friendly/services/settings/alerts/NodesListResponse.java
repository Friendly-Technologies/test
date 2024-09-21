package com.friendly.services.settings.alerts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NodesListResponse {
    List<NodeResponse> items;
}
