package com.friendly.services.settings.notification.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.NotificationInfo;
import com.friendly.services.settings.notification.orm.iotw.model.NotificationInfoEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationInfo notificationEntityToNotificationInfo(NotificationInfoEntity notificationInfo) {
        return NotificationInfo.builder()
                .byEmail(notificationInfo.isByEmail())
                .bySms(notificationInfo.isBySms())
                .checkedEvents(notificationInfo.getCheckedEvents())
                .phones(notificationInfo.getPhones())
                .emails(notificationInfo.getEmails())
                .sendEvents(notificationInfo.isSendEvents())
                .sendMonitoring(notificationInfo.isSendMonitoring())
                .sendUG(notificationInfo.isSendUG())
                .soonMinutes(notificationInfo.getSoonMinutes())
                .subject(notificationInfo.getSubject())
                .build();
    }

    public NotificationInfoEntity notificationInfoToNotificationEntity(ClientType clientType,
                                                                       NotificationInfo notificationInfo) {

        return NotificationInfoEntity.builder()
                .id(clientType)
                .byEmail(notificationInfo.isByEmail())
                .bySms(notificationInfo.isBySms())
                .checkedEvents(notificationInfo.getCheckedEvents())
                .phones(notificationInfo.getPhones())
                .emails(notificationInfo.getEmails())
                .sendEvents(notificationInfo.isSendEvents())
                .sendMonitoring(notificationInfo.isSendMonitoring())
                .sendUG(notificationInfo.isSendUG())
                .soonMinutes(notificationInfo.getSoonMinutes())
                .subject(notificationInfo.getSubject())
                .build();
    }
}
