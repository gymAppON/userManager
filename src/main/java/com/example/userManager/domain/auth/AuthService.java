package com.example.userManager.domain.auth;

import com.example.userManager.domain.auth.dto.LoginRequestDto;
import com.example.userManager.domain.auth.dto.SignupRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;

public interface AuthService {
    UserResponseDto register(SignupRequestDto request);

    String login(LoginRequestDto loginRequestDto) throws Exception;

    String loginViaGoogle(String name, LoginRequestDto loginRequestDto);

    UserResponseDto emailVerification(String emailVerificationCode);

    UserResponseDto passwordVerification(String passwordVerificationCode);
}
