package com.friendly.services.filemanagement.orm.acs.model;

import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "files_ftp")
@Data
@SuperBuilder
@NoArgsConstructor
@IdClass(FilesFtpPK.class)
public class FilesFtpEntity implements Serializable {

    @Id
    @Column(name = "file_name")
    private String fileName;

    @Id
    @Column(name = "location_id")
    private Integer domainId;

    @Id
    @Column(name = "group_id")
    private Long groupId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductClassGroupEntity productClassGroup;

    @Column(name = "cpe_id")
    private Long cpeId;



    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "version")
    private String version;

    @Column(name = "file_type")
    private Integer fileTypeId;

    @Column(name = "file_date")
    private Instant fileDate;

    @Column(name = "newest")
    private Boolean newest;

    @Formula("(select case when d.name is null then 'Super domain' else d.name end from files_ftp c " +
            "left join isp d on c.location_id = d.id where c.file_name = file_name and c.location_id=location_id and c.group_id=group_id)")
    private String domainName;
}
