package com.friendly.commons.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that defines a User Role Permission
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    private Long id;
    private String name;
    private String path;
    private String type;
    private String iconPath;
    private String location;
    private PermissionState viewState;
    private PermissionState executeState;

    private List<Permission> permissions;

    @JsonIgnore
    private Integer index;
}