package org.example.emailnotificationservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.emailnotificationservice.dtos.EmailNotificationDTO;
import org.example.emailnotificationservice.utils.EmailNotificationUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@Component
public class SendEmailNotificationConsumer {
    private final ObjectMapper objectMapper;

    public SendEmailNotificationConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "onboarding", groupId = "emailNotificationService")
    public void sendOnboardingEmail(String message) {
        try {
            EmailNotificationDTO emailNotificationDTO = objectMapper.readValue(message, EmailNotificationDTO.class);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailNotificationDTO.getFromEmail(), "ptykogfbncyuyggj");
                }
            };
            Session session = Session.getInstance(props, auth);

            EmailNotificationUtil.sendEmail(
                    session,
                    emailNotificationDTO.getToEmail(),
                    emailNotificationDTO.getSubject(),
                    emailNotificationDTO.getBody()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
