package com.friendly.services.qoemonitoring.orm.qoe.model;


import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kpi_data")
@Entity
@Data
@IdClass(KpiDataEntityId.class)
public class KpiDataEntity implements Serializable {
    @Id
    private Instant created;

    @Column(name = "serial")
    private String serial;

    @Column(name = "kpi_id")
    private Long kpiId;

    @Column(name = "value")
    private String value;
}
