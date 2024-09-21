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
@Table(name = "event_hardcoded_url")
@Data
@SuperBuilder
@NoArgsConstructor
public class EventHardcodedUrlEntity extends AbstractEntity<Integer> {

    @Column(name = "event_hardcoded_id")
    private Integer eventId;
    @Column(name = "url_id")
    private Integer urlId;
    @Column(name = "isp_id")
    private Integer ispId;
}
