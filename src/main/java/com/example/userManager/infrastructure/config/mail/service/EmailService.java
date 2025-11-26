package com.example.userManager.infrastructure.config.mail.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendLetter(String to, String subject, String text);

    void sendVerificationEmailLetter(String to, String emailVerificationCode);

    void sendVerificationPasswordLetter(String to, String passwordVerificationCode);

    void sendHTMLEmail(String to, String subject, String htmlText, String imageName) throws MessagingException;
}