package com.myproject.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private final String FRONTEND_URL = "http://localhost:8080/api/users/verify?token=";


    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verify Your Email";
        String body = "Click the link to verify your email: " + FRONTEND_URL +token;

        sendEmail(toEmail, subject, body);
    }

    public void sendSuccessEmail(String toEmail) {
        String subject = "Registration Successful";
        String body = "Congratulations! Your registration is successful.";

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}