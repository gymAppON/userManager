package com.example.userManager.domain.user.dto;

import com.example.userManager.domain.user.UserMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDto(@NotBlank @Size(min = 2, max = 50) String username,
                             @Nullable @Schema(example = "string@gmail.com") @Email String email,
                             @Nullable String telegramId,
                             @Nullable String googleAuthId,
                             @NotBlank @Size(min = 8, max = 100) String password,
                             @NotNull UserMetadata metadata) {
}
