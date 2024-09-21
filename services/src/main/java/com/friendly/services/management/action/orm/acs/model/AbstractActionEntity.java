package com.friendly.services.management.action.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractActionEntity implements Serializable {

    @Column(name = "updated")
    private Instant updated;

    @Column(name = "updator")
    private String updator;

    @Column(name = "created")
    private Instant created;

    @Column(name = "creator")
    private String creator;

//    @PrePersist
//    protected void onCreate() {
//        this.created = Instant.now();
//        this.updated = Instant.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.updated = Instant.now();
//    }
}
