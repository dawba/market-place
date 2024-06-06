package org.marketplace.services;

import org.marketplace.builders.EmailBuilder;
import org.marketplace.enums.AdvertisementStatus;
import org.marketplace.models.Advertisement;
import org.marketplace.models.Email;
import org.marketplace.models.User;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

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
            throw new RuntimeException("An unexpected error occurred while sending email " + e.getMessage(), e);
        }
    }

    /**
     * Sends an email to all observers of an advertisement when the price changes
     * @param observers list of emails to send the email to (observers of the advertisement)
     * @param price new price of the advertisement
     * @param adTitle title of the advertisement we refer to
     */
    public void sendEmailToObservers(List<String> observers, Double price, String adTitle){
        for(String observer: observers){
            sendPriceChangeEmail(observer, adTitle, price);
        }
    }

    /**
     * Sends an email to all observers of an advertisement
     * @param observers list of emails to send the email to (observers of the advertisement)
     * @param status status of the advertisement (ACTIVE, INACTIVE, BOUGHT, DELETED)
     * @param adTitle title of the advertisement we refer to
     */
    public void sendEmailToObservers(List<String> observers, AdvertisementStatus status, String adTitle){
        for(String observer: observers){
            switch(status){
                case ACTIVE:
                    sendActiveEmail(observer, adTitle);
                    break;
                case INACTIVE:
                    sendInactiveEmail(observer, adTitle);
                    break;
                case BOUGHT:
                    sendBoughtEmail(observer, adTitle);
                    break;
                case DELETED:
                    sendDeletedEmail(observer, adTitle);
                    break;
            }
        }
    }

    /**
     * Sends an email to the owner of an advertisement when it is bought
     * @param user owner of the advertisement
     * @param ad advertisement that has been bought
     * @param buyer email of the buyer
     */
    public void sendEmailToOwner(User user, Advertisement ad, String buyer){
        Email ownerMailMessage = new EmailBuilder()
                .setTo(user.getEmail())
                .setSubject("Your advertisement has been bought!")
                .setContent("Hello " + user.getLogin() + ",\n\nYour advertisement with title: " + ad.getTitle() + " has been bought by user: " + buyer)
                .build();

        sendEmail(ownerMailMessage);
    }

    private void sendInactiveEmail(String email, String adTitle){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Advertisement" + adTitle + " is now inactive!")
                .setContent("Hello " + email + ",\n\nThe advertisement you are observing is now inactive and no longer available for purchase.")
                .build();

        sendEmail(mailMessage);
    }

    private void sendBoughtEmail(String email, String adTitle){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Advertisement " + adTitle + "has been bought!")
                .setContent("Hello " + email + ",\n\nThe advertisement you are observing has been bought and is no longer available.")
                .build();

        sendEmail(mailMessage);
    }

    private void sendDeletedEmail(String email, String adTitle){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Advertisement " + adTitle + "has been deleted!")
                .setContent("Hello " + email + ",\n\nThe advertisement you are observing has been deleted and is no longer available.")
                .build();

        sendEmail(mailMessage);
    }

    private void sendPriceChangeEmail(String email, String adTitle, double price){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Price change for advertisement " + adTitle)
                .setContent("Hello " + email + ",\n\nThe price for the advertisement " + adTitle + " has been changed to " + price)
                .build();

        sendEmail(mailMessage);
    }

    private void sendActiveEmail(String email, String adTitle){
        Email mailMessage = new EmailBuilder()
                .setTo(email)
                .setSubject("Advertisement " + adTitle + "is now active!")
                .setContent("Hello " + email + ",\n\nThe advertisement you are observing is now active and available for purchase.")
                .build();

        sendEmail(mailMessage);
    }
}
