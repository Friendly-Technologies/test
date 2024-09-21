package com.friendly.services.infrastructure.utils.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Getter @Setter
public class FTJavaMailSender extends JavaMailSenderImpl {

    private String from;
    private String subject;

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        simpleMessage.setFrom(getFrom());
        simpleMessage.setSubject(getSubject());
        send(new SimpleMailMessage[] {simpleMessage});
    }
}
