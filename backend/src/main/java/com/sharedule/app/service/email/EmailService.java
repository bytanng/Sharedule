package com.sharedule.app.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${spring.mail.from}")
    private String fromEmail;
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(this.fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            this.javaMailSender.send(message);
        } catch (Exception var5) {
            Exception e = var5;
            e.printStackTrace();
            throw new RuntimeException("Error sending email");
        }
    }
}
