package com.friendly.services.management.groupupdate.orm.acs.model;

import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateDeviceStateType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ug_cpe_completed_his")
public class UpdateGroupCompletedHistory extends AbstractEntity<Integer> {

    Integer cpeId;
    GroupUpdateDeviceStateType state;
    Integer ugChildId;
    Timestamp historyTime;
    @Column(name = "ug_id")
    Integer updateGroupId;
    String creator;
    Instant created;
    Instant updated;
    Instant updator;
}
