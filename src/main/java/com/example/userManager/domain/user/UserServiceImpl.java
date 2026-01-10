package com.example.userManager.domain.user;

import com.example.userManager.domain.user.dto.UserRequestDto;
import com.example.userManager.domain.auth.dto.SignupRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;
import com.example.userManager.infrastructure.config.mail.notifications.UserSecurityCodeUpdatedEvent;
import com.example.userManager.shared.enums.VerificationType;
import com.example.userManager.shared.exception.LogEnum;
import com.example.userManager.shared.exception.exceptions.general.CustomAlreadyExistException;
import com.example.userManager.shared.exception.exceptions.general.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;
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
        user.setPasswordVerified(true);

        user.setEmailVerified(false);
        user.setEmailVerificationCode(UUID.randomUUID().toString().substring(0, 6));
        eventPublisher.publishEvent(new UserSecurityCodeUpdatedEvent(email, user.getEmailVerificationCode(), VerificationType.EMAIL_UPDATE));

        UserEntity savedUserEntity = userRepository.save(user);

        log.info("{}: {} (Id: {}) was created", LogEnum.SERVICE, OBJECT_NAME, savedUserEntity.getId());
        return userMapper.toResponse(savedUserEntity);
    }

    @Override
    public UserResponseDto getById(UUID id) {
        UserEntity userEntity = findById(id);

        log.info("{}: {} (Id: {}) was found", LogEnum.SERVICE, OBJECT_NAME, id);
        return userMapper.toResponse(userEntity);
    }

    @Override
    public List<UserResponseDto> getAll() {
        List<UserResponseDto> users = userMapper.toResponseDtoList(userRepository.findAll());

        log.info("{}: all {} were obtained", LogEnum.SERVICE, OBJECT_NAME);
        return users;
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestDto request) {
        UserEntity fromDb = findById(id);
        String emailRequest = request.email();
        String passwordRequest = request.password();
        String googleAuthId = request.googleAuthId();
        String telegramId = request.telegramId();
        UserEntity fromRequest = userMapper.toEntity(request);

        if (emailRequest!=null && !emailRequest.equals(fromDb.getEmail())) {
            if (userRepository.existsByEmail(emailRequest)){
                throw new CustomAlreadyExistException(OBJECT_NAME, "Email", emailRequest);
            }
            fromRequest.setEmailVerified(false);
            fromRequest.setEmailVerificationCode(UUID.randomUUID().toString().substring(0, 6));
            eventPublisher.publishEvent(new UserSecurityCodeUpdatedEvent(emailRequest, fromRequest.getEmailVerificationCode(), VerificationType.EMAIL_UPDATE));
        }
        if (!passwordEncoder.matches(passwordRequest, fromDb.getPassword())) {
            fromRequest.setPassword(passwordEncoder.encode(passwordRequest));
            fromRequest.setPasswordVerified(false);
            fromRequest.setPasswordVerificationCode(UUID.randomUUID().toString().substring(0, 6));
            eventPublisher.publishEvent(new UserSecurityCodeUpdatedEvent(emailRequest, fromRequest.getPasswordVerificationCode(), VerificationType.PASSWORD_UPDATE));
        }

        if (googleAuthId!=null && !googleAuthId.equals(fromDb.getGoogleAuthId())) {
            if (userRepository.existsByGoogleAuthId(googleAuthId)){
                throw new CustomAlreadyExistException(OBJECT_NAME, "Google Auth Id", googleAuthId);
            }
        }
        if (telegramId!=null && !telegramId.equals(fromDb.getTelegramId())) {
            if (userRepository.existsByTelegramId(telegramId)){
                throw new CustomAlreadyExistException(OBJECT_NAME, "Telegram Id", telegramId);
            }
        }

        log.info("{}: {} (Id: {}) was updated", LogEnum.SERVICE, OBJECT_NAME, id);
        return userMapper.toResponse(userRepository.save(fromRequest));
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);

        log.info("{}: {} (Id: {}) was deleted", LogEnum.SERVICE, OBJECT_NAME, id);
    }

    //We need to invent the field, which will have such characteristics:
    //1) Will be unique
    //2) Every user will have it filled
    // It's needed to make further security logic and just to have a one "for sure" available field to get user
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    //FIND BY
    private UserEntity findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomNotFoundException(OBJECT_NAME, id));
    }

    @Override
    public UserEntity findByEmail (String email) {
        log.info("{}: request on retrieving " + OBJECT_NAME + " by email {} was sent", LogEnum.SERVICE, email);
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomNotFoundException(OBJECT_NAME, email));
    }

    public UserEntity findByGoogleAuthId (String googleId) {
        log.info("{}: request on retrieving " + OBJECT_NAME + " by googleId {} was sent", LogEnum.SERVICE, googleId);
        return userRepository.findByGoogleAuthId(googleId).orElseThrow(() -> new CustomNotFoundException(OBJECT_NAME, googleId));
    }

    public UserEntity findByTelegramId (String telegramId) {
        log.info("{}: request on retrieving " + OBJECT_NAME + " by telegramId {} was sent", LogEnum.SERVICE, telegramId);
        return userRepository.findByTelegramId(telegramId).orElseThrow(() -> new CustomNotFoundException(OBJECT_NAME, telegramId));
    }

    @Override
    public UserEntity findByContactInfo(String contact){
        log.info("{}: searching for user by any contact info: {}", LogEnum.SERVICE, contact);

        return userRepository.findByEmail(contact)
                .or(() -> userRepository.findByGoogleAuthId(contact))
                .or(() -> userRepository.findByTelegramId(contact))
                .orElseThrow(()->new CustomNotFoundException(OBJECT_NAME, contact));
    }

    public UserEntity findByEmailVerificationCode(String verificationCode) {
        log.info("{}: request on retrieving " + OBJECT_NAME + " by email verification code {} was sent", LogEnum.SERVICE, verificationCode);
        return userRepository.findByEmailVerificationCode(verificationCode).orElseThrow(() -> new CustomNotFoundException(OBJECT_NAME));
    }

    public UserEntity findByPasswordVerificationCode(String verificationCode) {
        log.info("{}: request on retrieving " + OBJECT_NAME + " by password verification code {} was sent", LogEnum.SERVICE, verificationCode);
        return userRepository.findByPasswordVerificationCode(verificationCode).orElseThrow(() -> new CustomNotFoundException(OBJECT_NAME));
    }

    //CONFIRMATION
    public UserResponseDto confirmEmail(String emailVerificationCode) {
        UserEntity user = findByEmailVerificationCode(emailVerificationCode);
        user.setEmailVerified(true);
        user.setEmailVerificationCode(null);
        log.info("{}: " + OBJECT_NAME + "'s (id: {}) email has been confirmed", LogEnum.SERVICE, user.getId());
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponseDto confirmPassword(String passwordVerificationCode) {
        UserEntity user = findByPasswordVerificationCode(passwordVerificationCode);
        user.setPasswordVerified(true);
        user.setPasswordVerificationCode(null);
        log.info("{}: " + OBJECT_NAME + "'s (id: {}) password has been confirmed", LogEnum.SERVICE, user.getId());
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
