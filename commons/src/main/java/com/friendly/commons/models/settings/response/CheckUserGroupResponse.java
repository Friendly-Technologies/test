package com.friendly.commons.models.settings.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckUserGroupResponse {
    private boolean exist;
    private List<Integer> ids;
}
