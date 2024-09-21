package com.friendly.services.filemanagement.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilesFtpPK implements Serializable {

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "location_id")
    private Integer domainId;

    @Column(name = "group_id")
    private Long groupId;

}
