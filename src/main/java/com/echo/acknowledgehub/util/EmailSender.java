package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.dto.EmailDTO;
import com.echo.acknowledgehub.exception_handler.EmailSenderException;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class EmailSender {
    private static final Logger LOGGER = Logger.getLogger(EmailSender.class.getName());
    private static final Set<String> ALLOWED_DOMAINS = new HashSet<>();
    //  private final JavaMailSender JAVA_MAIL_SENDER;

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
    public CompletableFuture<Void> sendEmail(EmailDTO email) throws IOException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("xxeno245@gmail.com", "ijdv bvib hwxl kbfo");
                    }
                });

        try {
            LOGGER.info("email address : " + email.getAddress());
            if (isDomainAvailable(email.getAddress())) {
                Message message = new MimeMessage(session);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getAddress()));
                message.setSubject(email.getSubject());

                // HTML content with Myanmar font
                String htmlContent = "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<style>" +
                        "@import url('https://fonts.googleapis.com/css2?family=Myanmar+Sans:wght@400;700&display=swap');" +
                        "body { font-family: 'Myanmar Sans', sans-serif; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        email.getMessage()+
                        "</body>" +
                        "</html>";



                // Create the HTML part
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

                // Create a multipart message
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(htmlPart);

                // Attach file if exists
                if (email.getFile() != null && !email.getFile().isEmpty()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(email.getFile().getInputStream(), email.getFile().getContentType())));
                    attachmentPart.setFileName(email.getFile().getOriginalFilename());
                    multipart.addBodyPart(attachmentPart);
                }

                // Set the complete message parts
                message.setContent(multipart);

                // Send message
                Transport.send(message);
                return CompletableFuture.completedFuture(null);
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
    private boolean isDomainAvailable(String domain) {
        return ALLOWED_DOMAINS.stream().anyMatch(domain::endsWith);
    }
}
