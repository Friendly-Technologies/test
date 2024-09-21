package com.friendly.commons.models.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class NotificationInfo {
    boolean sendUG;
    boolean sendEvents;
    boolean sendMonitoring;
    boolean byEmail;
    boolean bySms;
    List<String> emails;
    List<String> phones;
    List<ScheduledEvent> checkedEvents;
    Integer soonMinutes;
    String subject;
}
