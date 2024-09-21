package com.friendly.services.management.groupupdate.dto.response;

import com.friendly.services.management.groupupdate.dto.GroupUpdateDevice;
import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateReport implements Serializable {

    private Integer id;
    private String name;
    private String domain;
    private String creator;
    private String created;
    private Instant createdIso;
    private String updated;
    private Instant updatedIso;
    private String activated;
    private Instant activatedIso;
    private GroupUpdateStateType state;
    private List<GroupUpdateDevice> groups;

}
