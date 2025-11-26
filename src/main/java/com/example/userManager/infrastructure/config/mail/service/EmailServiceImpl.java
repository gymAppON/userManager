package com.example.userManager.infrastructure.config.mail.service;

import com.example.userManager.shared.exception.LogEnum;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public static final String OBJECT_NAME = "Mail";

    private String subject;
    private String text;

    @Override
    public void sendLetter(String to, String subject, String text) {
        sendSimpleMessage(to, subject, text);
        log.info("{}: " + OBJECT_NAME + " (to: {}) was send", LogEnum.SERVICE, to);
    }

    @Override
    public void sendVerificationEmailLetter(String to, String verificationCode) {
        subject = "Email verification in the GymApp";
        text = "You need to verify your account. Your verification code: %s";

        sendSimpleMessage(to, subject, String.format(text, verificationCode));
        log.info("{}: Email verification " + OBJECT_NAME + " (to: {}) was send", LogEnum.SERVICE, to);
    }

    @Override
    public void sendVerificationPasswordLetter(String to, String verificationCode) {
        subject = "Verify password update in the GymApp";
        text = "You need to verify your account password update. Your verification code: %s";

        sendSimpleMessage(to, subject, String.format(text, verificationCode));
        log.info("{}: Password verification " + OBJECT_NAME + " (to: {}) was send", LogEnum.SERVICE, to);
    }

    @Override
    public void sendHTMLEmail(String to, String subject, String htmlText, String imageName) throws MessagingException {
        sendHTMLMessage(to, subject, htmlText, imageName);
        log.info("{}: HTML " + OBJECT_NAME + " (to: {}) was send", LogEnum.SERVICE, to);
    }

    private void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private void sendHTMLMessage(String to, String subject, String htmlText, String imageName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        helper.setFrom(mailUsername);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlText, true);

        if (!imageName.isBlank()) {
            ClassPathResource imageResource = new ClassPathResource("images/"+imageName);
            helper.addInline(imageName, imageResource);
        }
        mailSender.send(mimeMessage);
    }
}