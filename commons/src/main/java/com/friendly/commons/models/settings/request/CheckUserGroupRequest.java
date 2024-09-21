package com.friendly.commons.models.settings.request;

import lombok.Getter;

import java.util.List;

@Getter
public class CheckUserGroupRequest {
    private List<Integer> ids;
}
