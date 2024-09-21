package com.friendly.services.uiservices.frame.orm.iotw.model;

import com.friendly.commons.models.view.DataFormatType;
import com.friendly.commons.models.view.InputType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Model that represents persistence version of Relations with View and Frame
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_view_frame_param_detals")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FrameParamDetailsEntity extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_id")
    private FrameParamEntity param;

    @Column(name = "title_index")
    private Integer index;

    @Column(name = "full_names")
    private String fullNames;

    @Column(name = "input_type")
    @Enumerated(EnumType.STRING)
    private InputType inputType;

    @Column(name = "data_format")
    @Enumerated(EnumType.STRING)
    private DataFormatType dataFormatType;

    @Column(name = "required")
    private Boolean required;

    //text, integer
    @Column(name = "black_list")
    private String blackList;

    @Column(name = "white_list")
    private String whiteList;

    //integer
    @Column(name = "scale")
    private String scale;

    @Column(name = "min_value")
    private Integer minValue;

    @Column(name = "max_value")
    private Integer maxValue;

    //radio, select
    @Column(name = "options")
    private String options;

}
