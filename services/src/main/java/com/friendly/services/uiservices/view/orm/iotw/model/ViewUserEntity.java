package com.friendly.services.uiservices.view.orm.iotw.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Model that represents persistence version of Column Name
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Entity
@Table(name = "iotw_view_user")
@Data
@IdClass(ViewUserPK.class)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ViewUserEntity implements Serializable {

    @Id
    @Column(name = "view_id", nullable = false)
    private Long viewId;

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

}
