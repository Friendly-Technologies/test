package com.friendly.services.uiservices.frame.orm.iotw.model;

import com.friendly.commons.models.view.ConditionLogic;
import com.friendly.commons.models.view.ConditionType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Model that represents persistence version of Column Name
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_view_frame_condition")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FrameConditionEntity extends AbstractEntity<Long> {

    @Column(name = "view_id", nullable = false)
    private Long viewId;

    @Column(name = "column_key", nullable = false)
    private String columnKey;

    @Column(name = "logic")
    @Enumerated(EnumType.STRING)
    private ConditionLogic logic;

    @Column(name = "compare")
    @Enumerated(EnumType.STRING)
    private ConditionType compare;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "date_value")
    private Instant dateValue;

}
