package org.marketplace.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marketplace.models.Email;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_SuccessfullySent() {
        // Given
        Email email = new Email("from@example.com", "to@example.com", "Subject", "Content");

        // When
        assertDoesNotThrow(() -> emailService.sendEmail(email));

        // Then
        verify(javaMailSender, times(1)).send(email);
    }

    @Test
    void sendEmail_NoRecipients_ThrowsException() {
        // Given
        Email email = new Email();

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendEmail(email));

        // Then
        assertEquals("An unexpected error occurred while sending email Email must have a recipient", exception.getMessage());
        verify(javaMailSender, times(0)).send(email);
    }

    @Test
    void sendEmail_MailSendException_ThrowsMailSendException() {
        // Given
        Email email = new Email("from@example.com", "to@example.com", "Subject", "Content");
        doThrow(new MailSendException("Failed to send email")).when(javaMailSender).send(email);
        // When
        MailSendException exception = assertThrows(MailSendException.class, () -> emailService.sendEmail(email));

        // Then
        assertEquals("Failed to send email", exception.getMessage());
    }

    @Test
    void sendEmail_UnexpectedException_ThrowsRuntimeException() {
        // Given
        Email email = new Email("from@example.com", "to@example.com", "Subject", "Content");
        doThrow(new RuntimeException("Unexpected error")).when(javaMailSender).send(email);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendEmail(email));

        // Then
        assertEquals("An unexpected error occurred while sending email Unexpected error", exception.getMessage());
    }
}
