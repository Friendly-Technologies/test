package com.friendly.services.management.events.orm.acs.model;

import com.friendly.services.infrastructure.base.model.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "event_hardcoded")
@Data
@SuperBuilder
@NoArgsConstructor
public class EventHardcodedEntity extends AbstractEntity<Integer> {

    @Column(name = "name")
    private String name;
    @Column(name = "notification")
    private Boolean notification;
    @Column(name = "description")
    private String description;
}
