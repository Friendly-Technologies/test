package com.friendly.services.qoemonitoring.orm.iotw.model;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.response.ModeType;
import com.friendly.commons.models.device.response.SortParameter;
import com.friendly.commons.models.device.response.SortType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "iotw_qoe_parameter_frame")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class QoeFrameItemEntity extends AbstractEntity<Long> {

    @Column(name = "name_id")
    private Integer nameId;

    @Column(name = "name")
    private String name;

    @Column(name = "protocol_id")
    @Enumerated
    private ProtocolType protocol;

    @Column(name = "height")
    private Integer height;

    @Column(name = "term")
    private Integer period;

    @Column(name = "sort_dir")
    @Enumerated(value = EnumType.STRING)
    private SortType sortDir;

    @Column(name = "sort_param")
    @Enumerated(value = EnumType.STRING)
    private SortParameter sortParam;

    @Column(name = "mode")
    @Enumerated
    private ModeType mode;

    @Formula("(SELECT n.kpi_name FROM ftacs_qoe_ui.kpi n where n.id = name_id)")
    private String parameterName;

}
