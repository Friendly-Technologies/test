package com.friendly.services.management.action.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonitorActionResponse {
    List<ActionListResponse> items;
}
