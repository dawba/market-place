package org.marketplace.services;

import org.marketplace.models.Email;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;

@Service("emailService")
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Async
    public void sendEmail(Email email){
        logger.info("Sending email to: " + Arrays.toString(email.getTo()));

        try {
            if(email.getTo() == null || email.getTo().length == 0){
                throw new IllegalArgumentException("Email must have a recipient");
            }

            javaMailSender.send(email);
        } catch(MailSendException e){
            String errorMessage = e.getMessage() == null ? "Error sending email" : e.getMessage();
            throw new MailSendException(errorMessage);
        } catch(Exception e){
            throw new RuntimeException("An unexpected error occurred while sending email" + e.getMessage(), e);
        }
    }
}
