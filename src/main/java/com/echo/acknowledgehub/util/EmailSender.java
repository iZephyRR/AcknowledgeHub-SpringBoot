package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.exception_handler.EmailSenderException;
import com.echo.acknowledgehub.dto.EmailDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class EmailSender {
    private static final Logger LOGGER = Logger.getLogger(EmailSender.class.getName());
    private static final Set<String> ALLOWED_DOMAINS = new HashSet<>();

    static {
        ALLOWED_DOMAINS.add("@gmail.com");
        ALLOWED_DOMAINS.add("@yahoo.com");
        ALLOWED_DOMAINS.add("@outlook.com");
        ALLOWED_DOMAINS.add("@hotmail.com");
        ALLOWED_DOMAINS.add("@aol.com");
        ALLOWED_DOMAINS.add("@icloud.com");
        ALLOWED_DOMAINS.add("@protonmail.com");
        ALLOWED_DOMAINS.add("@yandex.com");
        ALLOWED_DOMAINS.add("@mail.com");
        ALLOWED_DOMAINS.add("@zoho.com");
        ALLOWED_DOMAINS.add("@aceinspiration.com");
    }
    @Async
    public CompletableFuture<String> sendEmail(EmailDTO email) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("xxeno245@gmail.com", "ijdv bvib hwxl kbfo");
                    }
                });
        try {
            if (isDomainAvailable(email.getAddress())) {
                Message message = new MimeMessage(session);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getAddress()));
                message.setSubject(email.getSubject());
                message.setText(email.getMessage());
                Transport.send(message);
                return CompletableFuture.completedFuture("Send an email to "+email.getAddress());
            } else {
                LOGGER.warning("Invalid email address.");
                throw new EmailSenderException("Invalid email address.");
            }
        } catch (MessagingException e) {
            LOGGER.severe("MessagingException : " + e);
            throw new EmailSenderException("Could not send email.");
        }

    }
    @Async
    public boolean isDomainAvailable(String domain) {
        return ALLOWED_DOMAINS.stream().anyMatch(domain::endsWith);
    }
}
