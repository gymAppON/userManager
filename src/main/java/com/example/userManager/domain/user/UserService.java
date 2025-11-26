package com.example.userManager.domain.user;

import com.example.userManager.domain.user.dto.UserRequestDto;
import com.example.userManager.domain.auth.dto.LoginRequestDto;
import com.example.userManager.domain.auth.dto.SignupRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(SignupRequestDto request);

    UserResponseDto getById(UUID id);

    List<UserResponseDto> getAll();

    UserResponseDto update(UUID id, UserRequestDto request);

    void delete(UUID id);

    UserEntity findByEmail(String email);

    UserEntity findByContactInfo(String contact);

    UserEntity findByEmailVerificationCode(String verificationCode);

    UserEntity findByPasswordVerificationCode(String verificationCode);
}