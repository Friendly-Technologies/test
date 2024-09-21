package com.friendly.services.infrastructure.utils.mail;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.config.provider.MailProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailSender {

    @NonNull
    MailProvider mailProvider;

    public void sendSimpleMessage(final ClientType clientType, final String to, final String text) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setText(text);

        final JavaMailSender mailSender = mailProvider.getMailSender(clientType);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
