package com.friendly.services.management.groupupdate.dto;

import com.friendly.services.management.groupupdate.dto.enums.ActivationMethod;
import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateGroup implements Serializable {

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
    private ActivationMethod activationMethod;
    private GroupUpdateStateType state;
    private String application;
}
