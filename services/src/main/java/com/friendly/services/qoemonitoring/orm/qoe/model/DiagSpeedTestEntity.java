package com.friendly.services.qoemonitoring.orm.qoe.model;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.friendly.services.qoemonitoring.orm.qoe.model.enums.DiagnosticType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "diag_speed_test")
@Entity
@Data
@IdClass(DiagSpeedTestEntityId.class)
public class DiagSpeedTestEntity implements Serializable {
    @Id
    private Instant created;

    @Column(name = "serial")
    private String serial;

    @Column(name = "value")
    private String value;

    @Column(name = "name")
    @Enumerated(value = EnumType.STRING)
    private DiagnosticType name;
}
