package com.friendly.services.qoemonitoring.orm.qoe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cpe_data")
@Entity
@Data
public class CpeDataEntity implements Serializable {
    @Id
    private Instant created;

    @Column(name = "serial")
    private String serial;

    @Column(name = "name_id")
    private Integer nameId;

    @Column(name = "value")
    private String value;
}
