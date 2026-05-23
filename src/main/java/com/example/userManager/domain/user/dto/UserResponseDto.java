package com.example.userManager.domain.user.dto;

import com.example.userManager.domain.user.UserMetadata;

import java.util.UUID;

public record UserResponseDto(UUID id,
                              String username,
                              String email,
                              String telegramId,
                              String googleAuthId,
                              boolean isEmailVerified,
                              UserMetadata metadata) {
}
