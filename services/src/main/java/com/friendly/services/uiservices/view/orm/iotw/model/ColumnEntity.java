package com.friendly.services.uiservices.view.orm.iotw.model;

import com.friendly.commons.models.OrderDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Model that represents persistence version of Column Name
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode
@Entity
@Table(name = "iotw_view_column")
@IdClass(ViewColumnPK.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnEntity implements Serializable {

    @Id
    @Column(name = "view_id", nullable = false)
    private Long viewId;

    @Id
    @Column(name = "column_key", nullable = false)
    private String columnKey;

    @Column(name = "visible_index")
    private Integer visibleIndex;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private OrderDirection direction;

}
