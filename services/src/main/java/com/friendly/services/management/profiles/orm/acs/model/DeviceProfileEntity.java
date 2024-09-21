package com.friendly.services.management.profiles.orm.acs.model;

import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.management.profiles.entity.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Table(name = "profile")
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProfileEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

    @Column(name = "name")
    private String name;

    @Column(name = "group_id")
    private Long groupId;

    private Integer root;

    @Column(name = "send_provision")
    private Boolean sendProvision;

    @Column(name = "send_backup")
    private Boolean sendBackup;

    @Column(name = "updator")
    private String updater;

    @Column(name = "version")
    private String version;

    @Column(name = "location_id")
    private Integer domainId;

    @Column(name = "is_active")
    @Enumerated
    private ProfileStatus status;

    @Formula("(select f.filter_id from profile_filter f where f.profile_id = id)")
    private Long filterId;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductClassGroupEntity productClassGroup;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<DeviceProfileParameterEntity> parameters;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<DeviceProfileParameterAccessEntity> accesses;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<DeviceProfileParameterNotificationEntity> notifications;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<DeviceProfileParameterMonitorEntity> parameterMonitors;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<DeviceProfileEventMonitorEntity> eventMonitors;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<DeviceProfileFileEntity> files;

}
