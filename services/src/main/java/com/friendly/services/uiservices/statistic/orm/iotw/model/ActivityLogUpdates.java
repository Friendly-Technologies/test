package com.friendly.services.uiservices.statistic.orm.iotw.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.friendly.services.management.groupupdate.dto.enums.ActivationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "activity_log_updates")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogUpdates implements Serializable {
    @Id
    private Integer id;

    @Column(name = "act_type")
    @Enumerated(EnumType.STRING)
    private ActivationType actType;

    @Column(name = "update_id")
    private Integer updateId;
}
