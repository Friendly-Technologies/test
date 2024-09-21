package com.friendly.services.settings.emailserver;

import com.friendly.commons.models.settings.EmailServer;
import com.friendly.services.infrastructure.utils.PasswordUtils;
import org.springframework.stereotype.Component;

@Component
public class StatisticNoteUtils {


    public String buildNoteCreateNewEmailServer(EmailServer emailServer) {
        return "Set Host=" + emailServer.getHost() + "; Port=" + emailServer.getPort() +
                "; Username=" + emailServer.getUsername() + "; Password=" +
                PasswordUtils.getHiddenPassword(emailServer.getPassword()) +
                "; From=" + emailServer.getFrom() + "; Subject=" + emailServer.getSubject() +
                "; EnableSSL=" + emailServer.isEnableSSL() + ";";
    }
}
