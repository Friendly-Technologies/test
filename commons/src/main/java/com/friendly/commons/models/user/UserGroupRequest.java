package com.friendly.commons.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Model that defines a User Role
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupRequest implements Serializable {

    private Long id;
    private String name;
    private UserGroupActType actionType;

    private Map<Long, PermissionRequest> permissions;

}
