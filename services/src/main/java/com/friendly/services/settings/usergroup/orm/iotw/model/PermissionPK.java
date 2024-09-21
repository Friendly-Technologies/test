package com.friendly.services.settings.usergroup.orm.iotw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionPK implements Serializable {

    private Long groupId;
    private Long permissionId;
    private PermissionStateType type;

}
