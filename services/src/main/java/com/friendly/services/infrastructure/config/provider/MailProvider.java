package com.friendly.services.infrastructure.config.provider;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.config.MailConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

import static com.friendly.commons.models.auth.ClientType.mc;
import static com.friendly.commons.models.auth.ClientType.sc;

@Component
@DependsOn("liquibaseIotw")
@Slf4j
public class MailProvider {
    private static final Map<ClientType, JavaMailSender> mailSenderMap = new EnumMap<>(ClientType.class);

    public MailProvider(MailConfig mailConfig) {
        try {
            mailSenderMap.put(mc, mailConfig.getMailSender(mc));
            mailSenderMap.put(sc, mailConfig.getMailSender(sc));
        } catch (Exception e) {
            log.warn("Mail server is undefined");
        }
    }

    public JavaMailSender getMailSender(final ClientType clientType) {
        return mailSenderMap.get(clientType);
    }
}
