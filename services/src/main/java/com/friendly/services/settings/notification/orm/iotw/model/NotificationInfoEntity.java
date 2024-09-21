package com.friendly.services.settings.notification.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.ScheduledEvent;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "iotw_notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationInfoEntity implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private ClientType id;

    @Column(name = "send_ug")
    private boolean sendUG;

    @Column(name = "send_events")
    private boolean sendEvents;

    @Column(name = "send_monitoring")
    private boolean sendMonitoring;

    @Column(name = "by_email")
    private boolean byEmail;

    @Column(name = "by_sms")
    private boolean bySms;

    @Column(name = "email")
    @ElementCollection
    @CollectionTable(name = "iotw_notification_email", joinColumns = @JoinColumn(name = "notification_id"))
    private List<String> emails;

    @Column(name = "phone")
    @ElementCollection
    @CollectionTable(name = "iotw_notification_phone", joinColumns = @JoinColumn(name = "notification_id"))
    private List<String> phones;

    @Column(name = "event")
    @ElementCollection
    @CollectionTable(name = "iotw_notification_event", joinColumns = @JoinColumn(name = "notification_id"))
    private List<ScheduledEvent> checkedEvents;

    @Column(name = "soon_minutes")
    private Integer soonMinutes;

    @Column
    private String subject;
}
