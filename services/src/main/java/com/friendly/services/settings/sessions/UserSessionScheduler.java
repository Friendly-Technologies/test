package com.friendly.services.settings.sessions;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserSessionScheduler {
    private final SessionService sessionService;

    public UserSessionScheduler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void startSessionMonitorJob(){
        sessionService.killSessionsByUserExpireTime();
    }

}
