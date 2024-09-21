package com.friendly.services.qoemonitoring.orm.qoe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.Instant;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExpHostEntityId implements Serializable {
    @Column(name = "created")
    private Instant created;
}
