package com.friendly.services.qoemonitoring.orm.qoe.model;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KpiDataEntityId implements Serializable {
    @Column(name = "created")
    private Instant created;
}
