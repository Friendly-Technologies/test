package com.friendly.services.management.action.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class ActionMethodsList {
    private List<ActionMethods> items;
}
