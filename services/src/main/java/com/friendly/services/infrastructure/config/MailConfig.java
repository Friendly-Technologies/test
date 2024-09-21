package com.friendly.services.infrastructure.config;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.EmailServer;
import com.friendly.services.infrastructure.utils.mail.FTJavaMailSender;
import com.friendly.services.settings.emailserver.EmailServerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Configuration
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MailConfig {

    @NonNull
    EmailServerService emailService;

    public JavaMailSender getMailSender(final ClientType clientType) {
        return getMailSender(clientType, "");
    }

    public JavaMailSender getMailSender(final ClientType clientType, final String customSubject) {
        final EmailServer emailServer = emailService.getEmailServerByClientType(clientType);
        final FTJavaMailSender mailSender = new FTJavaMailSender();
        mailSender.setHost(emailServer.getHost());
        mailSender.setPort(NumberUtils.isCreatable(emailServer.getPort())
                ? Integer.parseInt(emailServer.getPort())
                : 0);
        mailSender.setUsername(emailServer.getUsername());
        mailSender.setPassword(emailServer.getPassword());
        mailSender.setFrom(emailServer.getFrom());
        if(customSubject != null && !customSubject.isEmpty()) {
            mailSender.setSubject(customSubject);
        } else {
            mailSender.setSubject(emailServer.getSubject());
        }

        final Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", emailServer.isEnableSSL());
        props.put("mail.debug", "true");

        return mailSender;
    }
}
