package com.example.userManager.user;

import com.example.userManager.dto.request.UserRequestDto;
import com.example.userManager.dto.request.auth.LoginRequestDto;
import com.example.userManager.dto.request.auth.SignupRequestDto;
import com.example.userManager.dto.response.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(SignupRequestDto request);

    UserResponseDto getById(UUID id);

    List<UserResponseDto> getAll();

    UserResponseDto update(UUID id, UserRequestDto request);

    void delete(UUID id);

    String login(LoginRequestDto loginRequestDto) throws Exception;

    UserResponseDto loginViaGoogle(String name, LoginRequestDto loginRequestDto);
}