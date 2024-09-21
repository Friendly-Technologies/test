package com.friendly.services.uiservices.view.orm.iotw.model;

import com.friendly.commons.models.view.PropertyType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that represents persistence version of Relations with View and Frame
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Entity
@Table(name = "iotw_view_frame_rel")
@Data
@IdClass(ViewFramePK.class)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ViewFrameEntity implements Serializable {

    @Id
    @Column(name = "view_id", nullable = false)
    private Long viewId;

    @Id
    @Column(name = "frame_id", nullable = false)
    private Long frameId;

    @Column(name = "order_index", nullable = false)
    private Integer index;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private PropertyType propertyType;

}
