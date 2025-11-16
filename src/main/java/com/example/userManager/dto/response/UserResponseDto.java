package com.example.userManager.dto.response;

import com.example.userManager.user.UserMetadata;

import java.util.UUID;

public record UserResponseDto(UUID id,
                              String username,
                              String email,
                              String telegramId,
                              String googleAuthId,
                              String password,
                              UserMetadata metadata) {
}
