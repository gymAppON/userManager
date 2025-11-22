package com.example.userManager.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto (@Nullable @Email @Schema(example = "string@gmail.com") String email,
                               @Nullable String telegramId,
                               @Nullable String googleAuthId,
                               @NotBlank @Size(min = 8, max = 100) String password){
}