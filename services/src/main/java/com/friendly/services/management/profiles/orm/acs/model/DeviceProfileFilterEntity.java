package com.friendly.services.management.profiles.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "profile_filter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProfileFilterEntity implements Serializable {
    @Id
    @Column(name = "profile_id")
    private Integer profileId;
    @Column(name = "filter_id")
    private Long filterId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DeviceProfileEntity deviceProfile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_id", referencedColumnName = "id", insertable = false, updatable = false)
    private GroupConditionEntity groupCondition;
}
