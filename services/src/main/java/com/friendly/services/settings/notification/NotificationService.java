package com.friendly.services.settings.notification;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.commons.models.settings.NotificationInfo;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.notification.orm.iotw.model.NotificationInfoEntity;
import com.friendly.services.settings.notification.orm.iotw.repository.NotificationInfoRepository;
import com.friendly.services.settings.notification.mapper.NotificationMapper;
import com.friendly.services.uiservices.statistic.StatisticService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.friendly.commons.models.reports.UserActivityType.CONFIGURING_NOTIFICATIONS;

@Slf4j
@Service
public class NotificationService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final NotificationInfoRepository notificationInfoRepository;

    @NonNull
    private final NotificationMapper notificationMapper;

    @NonNull
    private final StatisticService statisticService;

    public NotificationService(@NonNull JwtService jwtService,
                               @NonNull NotificationInfoRepository notificationInfoRepository,
                               @NonNull NotificationMapper notificationMapper, @NonNull StatisticService statisticService) {
        this.jwtService = jwtService;
        this.notificationInfoRepository = notificationInfoRepository;
        this.notificationMapper = notificationMapper;
        this.statisticService = statisticService;
    }

    public NotificationInfo getNotificationInfo(final String token) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        return getNotificationInfo(clientType);
    }

    public NotificationInfo getNotificationInfo(final ClientType clientType) {
        return notificationInfoRepository.findById(clientType)
                .map(notificationMapper::notificationEntityToNotificationInfo)
                .orElse(null);
    }

    @Transactional
    public NotificationInfo updateNotificationInfo(String token, NotificationInfo notification) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        final NotificationInfoEntity notificationInfoEntity = notificationInfoRepository.saveAndFlush(
                notificationMapper.notificationInfoToNotificationEntity(clientType, notification));
        final NotificationInfo result = notificationMapper.notificationEntityToNotificationInfo(notificationInfoEntity);

        statisticService.addUserLogAct(UserActivityLog.builder()
                .userId(session.getUserId())
                .clientType(clientType)
                .activityType(CONFIGURING_NOTIFICATIONS)
                .note("Scheduled time=" + result.getSoonMinutes() +
                        "; BySms=" + result.isBySms() +
                        "; ByEmail=" + result.isByEmail() +
                        "; SendUg=" + result.isSendUG() +
                        "; SendEvents=" + result.isSendEvents() +
                        "; SendMonitoring=" + result.isSendMonitoring() +
                        "; Subject=" + result.getSubject())
                .build());

        return result;
    }
}
