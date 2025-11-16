package com.example.userManager.user;

import com.example.userManager.dto.request.UserRequestDto;
import com.example.userManager.dto.request.auth.LoginRequestDto;
import com.example.userManager.dto.request.auth.SignupRequestDto;
import com.example.userManager.dto.response.UserResponseDto;
import com.example.userManager.exception.LogEnum;
import com.example.userManager.exception.exceptions.general.CustomAlreadyExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    //public class UserServiceImpl implements UserService {
    //}
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    //private final EmailService emailService;
    private static final String OBJECT_NAME = "User";

    private PasswordEncoder passwordEncoder;
    @Autowired
    public void passwordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserResponseDto create(SignupRequestDto request) {
        String email = request.email();
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomAlreadyExistException(OBJECT_NAME, "email", email);
        }

        UserEntity user = userMapper.toEntity(request);
        //user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.password()));
        UserEntity savedUserEntity = userRepository.save(user);

//        String helloMesSubject = "Welcome from Urban Zen!";
//        String helloMesText = "Hello from Urban Zen marketplace, happy to see you on our marketplace!";

//        emailService.sendLetter(email, helloMesSubject, helloMesText);
//        emailService.sendVerificationEmailLetter(email, savedUserEntity.getEmailVerificationCode());
        log.info("{}: {} (Id: {}) was created", LogEnum.SERVICE, OBJECT_NAME, savedUserEntity.getId());
        return userMapper.toResponse(savedUserEntity);
    }

    @Override
    public UserResponseDto getById(UUID id) {
        return null;
    }

    @Override
    public List<UserResponseDto> getAll() {
        return List.of();
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestDto request) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public String login(LoginRequestDto loginRequestDto) throws Exception {
        return "";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
