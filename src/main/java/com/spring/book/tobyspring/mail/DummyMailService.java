package com.spring.book.tobyspring.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

@Slf4j
public class DummyMailService implements MailSender {

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        log.debug("전송");
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        log.debug("전송");
    }

}
