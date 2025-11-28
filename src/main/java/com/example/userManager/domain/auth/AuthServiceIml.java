package com.example.userManager.domain.auth;

import com.example.userManager.domain.auth.dto.LoginRequestDto;
import com.example.userManager.domain.auth.dto.SignupRequestDto;
import com.example.userManager.domain.user.*;
import com.example.userManager.domain.user.dto.UserRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;
import com.example.userManager.infrastructure.security.jwt.JwtService;
import com.example.userManager.shared.enums.LanguageEnum;
import com.example.userManager.shared.enums.WeightUnitEnum;
import com.example.userManager.shared.exception.LogEnum;
import com.example.userManager.shared.exception.exceptions.general.CustomNotFoundException;
import com.example.userManager.shared.exception.exceptions.user.UnconfirmedPasswordChangeException;
import com.example.userManager.shared.exception.exceptions.user.UnverifiedAccountException;
import com.example.userManager.shared.exception.exceptions.user.UserIncorrectPasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceIml implements AuthService{
    private final UserService userService;
    private final JwtService jwtService;
    private PasswordEncoder passwordEncoder;

    private static final String OBJECT_NAME = "Authentication";

    @Autowired
    public void passwordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto register(SignupRequestDto request) {
        return userService.create(request);
    }

    @Override
    public String login(LoginRequestDto loginRequestDto) throws Exception {
        String email = loginRequestDto.email();

        UserEntity user = userService.findByEmail(email);
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())){
            throw new UserIncorrectPasswordException(user.getUsername());
        }
        if (!user.isEmailVerified()) {
            throw new UnverifiedAccountException(email);
        } else if (!user.isPasswordVerified()) {
            throw new UnconfirmedPasswordChangeException(email);
        }

        log.info("{}: {} (Username: {}) was logged in manually", LogEnum.SERVICE, OBJECT_NAME, user.getUsername());
        return jwtService.generateToken(user.getId(), user.getEmail(), user.getUsername());
    }

    @Override
    public String loginViaGoogle(String name, LoginRequestDto requestDto){
        String googleAuthId = requestDto.googleAuthId();
        String email = requestDto.email();
        UserEntity userOptional;

        try {
            userOptional = userService.findByContactInfo(googleAuthId);
        }catch (CustomNotFoundException _){
            userOptional = userService.findByEmail(email);
        }

        if (userOptional == null) {
            SignupRequestDto newUser = new SignupRequestDto(name, email, null, googleAuthId,
                    googleAuthId, new UserMetadata(WeightUnitEnum.KG, LanguageEnum.EN, "+2"));
            // newUser.setRole(Role.USER);
            log.info("{}: {} (Email: {}) was created via Google", LogEnum.SERVICE, OBJECT_NAME, email);
            UserResponseDto savedUser = userService.create(newUser);
            return jwtService.generateToken(savedUser.id(), savedUser.email(), savedUser.username());
        } else {
            log.info("{}: {} (Email: {}) was logged in via Google", LogEnum.SERVICE, OBJECT_NAME, email);
            return jwtService.generateToken(userOptional.getId(), email, userOptional.getUsername());
        }
    }

    @Override
    public UserResponseDto emailVerification(String emailVerificationCode) {
        return userService.confirmEmail(emailVerificationCode);
    }

    @Override
    public UserResponseDto passwordVerification(String passwordVerificationCode) {
        return userService.confirmPassword(passwordVerificationCode);
    }
}
