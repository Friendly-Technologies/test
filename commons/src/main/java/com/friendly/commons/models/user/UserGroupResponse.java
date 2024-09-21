package com.friendly.commons.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

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
public class UserGroupResponse implements Serializable {

    private Long id;
    private String name;
    private String created;
    private String updated;
    private Instant createdIso;
    private Instant updatedIso;
    private String updater;
    private Integer templateVersion;

    private List<Permission> permissions;

}
