package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.CustomException.EmailSenderException;
import com.echo.acknowledgehub.controller.HomeController;
import com.echo.acknowledgehub.dto.EmailDTO;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class EmailSender {
    private static final Logger LOGGER = Logger.getLogger(EmailSender.class.getName());

    public void sendEmail(EmailDTO email) {
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
            Message message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getAddress()));
            message.setSubject(email.getSubject());
            message.setText(email.getMessage());
            Transport.send(message);
        } catch (AddressException e) {
            LOGGER.severe("Address Exception : "+e);
            throw new EmailSenderException("Invalid email address.");
        } catch (MessagingException e) {
            LOGGER.severe("MessagingException : "+e);
            throw new EmailSenderException("Could not send email.");
        }
    }
}
