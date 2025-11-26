package com.example.userManager.infrastructure.config.mail.notifications;

import com.example.userManager.shared.enums.VerificationType;

public record UserSecurityCodeUpdatedEvent(String email, String code, VerificationType type) {}