package com.example.userManager.domain.auth;

import com.example.userManager.domain.auth.dto.LoginRequestDto;
import com.example.userManager.domain.auth.dto.SignupRequestDto;
import com.example.userManager.domain.user.*;
import com.example.userManager.domain.user.dto.UserResponseDto;
import com.example.userManager.shared.enums.LanguageEnum;
import com.example.userManager.shared.enums.WeightUnitEnum;
import com.example.userManager.shared.exception.LogEnum;
import com.example.userManager.shared.exception.exceptions.general.CustomNotFoundException;
import com.example.userManager.shared.exception.exceptions.user.UserIncorrectPasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//Need to figure out what to use
//UserService or UserRepository
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceIml implements AuthService{
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
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
//        if (!user.isEmailVerified()) {
//            throw new UnverifiedAccountException(email);
//        } else if (!user.isPasswordVerified()) {
//            throw new UnconfirmedPasswordChangeException(email);
//        }

//        Authentication authentication;
//        try {
//            authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password())
//            );
//        } catch (AuthenticationException e) {
//            throw new Exception("Authentication Exception", e);
//        }
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        //return jwtService.generateToken(user.getId(), user.getEmail(), user.getFirstName()+" "+user.getLastName());
        log.info("{}: {} (Username: {}) was logged in manually", LogEnum.SERVICE, OBJECT_NAME, user.getUsername());
        return "JWT Token in Future";
    }

    @Override
    public UserResponseDto loginViaGoogle(String name, LoginRequestDto requestDto) {
        String googleAuthId = requestDto.googleAuthId();
        String email = requestDto.email();
        UserEntity userOptional = null;

        try {
            userOptional = userService.findByContactInfo(googleAuthId);
        }catch (CustomNotFoundException _){

        }

        if (userOptional == null) {
            SignupRequestDto newUser = new SignupRequestDto(name, email, null, googleAuthId,
                    googleAuthId, new UserMetadata(WeightUnitEnum.KG, LanguageEnum.EN, "+2"));
            // newUser.setRole(Role.USER);
            log.info("{}: {} (Email: {}) was created via Google", LogEnum.SERVICE, OBJECT_NAME, email);
            return userService.create(newUser);
        } else {
            userOptional.setUsername(name);

            log.info("{}: {} (Email: {}) was logged in via Google", LogEnum.SERVICE, OBJECT_NAME, email);
            return userMapper.toResponse(userRepository.save(userOptional));
        }
    }
}
