package com.example.userManager.infrastructure.config.mail.notifications;

import com.example.userManager.infrastructure.config.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;

    @EventListener
    public void handleSecurityUpdate(UserSecurityCodeUpdatedEvent event) {
        switch (event.type()) {
            case EMAIL_UPDATE -> emailService.sendVerificationEmailLetter(event.email(), event.code());
            case PASSWORD_UPDATE -> emailService.sendVerificationPasswordLetter(event.email(), event.code());
        }
    }
}