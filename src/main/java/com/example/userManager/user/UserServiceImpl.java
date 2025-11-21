package com.example.userManager.user;

import com.example.userManager.dto.request.UserRequestDto;
import com.example.userManager.dto.request.auth.LoginRequestDto;
import com.example.userManager.dto.request.auth.SignupRequestDto;
import com.example.userManager.dto.response.UserResponseDto;
import com.example.userManager.enums.LanguageEnum;
import com.example.userManager.enums.WeightUnitEnum;
import com.example.userManager.exception.LogEnum;
import com.example.userManager.exception.exceptions.general.CustomAlreadyExistException;
import com.example.userManager.exception.exceptions.general.CustomNotFoundException;
import com.example.userManager.exception.exceptions.user.UserIncorrectPasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        UserEntity userEntity = findById(id);
        String passwordRequest = request.password();
        String passwordInDb = userEntity.getPassword();

        validateUserUpdation(userEntity, request);
        userEntity = userMapper.toEntity(request);
        if (!passwordEncoder.matches(passwordRequest, passwordInDb)){
            userEntity.setPassword(passwordEncoder.encode(passwordRequest));
//            userEntity.setPasswordVerified(false);
//            userEntity.setPasswordVerificationCode(UUID.randomUUID().toString().substring(0, 6));
        }

        //updatedUser.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserEntity updatedUserEntity = userRepository.save(userEntity);

        log.info("{}: {} (Id: {}) was updated", LogEnum.SERVICE, OBJECT_NAME, id);
        return userMapper.toResponse(updatedUserEntity);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);

        log.info("{}: {} (Id: {}) was deleted", LogEnum.SERVICE, OBJECT_NAME, id);
    }

    @Override
    public String login(LoginRequestDto loginRequestDto) throws Exception {
        String email = loginRequestDto.email();

        UserEntity user = findByEmail(email);
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
        return "JWT Token in Future";
    }

    @Override
    public UserResponseDto loginViaGoogle(String name, LoginRequestDto requestDto) {
        String googleAuthId = requestDto.googleAuthId();
        String email = requestDto.email();
        Optional<UserEntity> userOptional = userRepository.findByGoogleAuthId(googleAuthId);

        if (userOptional.isEmpty()) {
            SignupRequestDto newUser = new SignupRequestDto(name, email, null, googleAuthId,
                    googleAuthId, new UserMetadata(WeightUnitEnum.KG, LanguageEnum.EN, "+2"));
           // newUser.setRole(Role.USER);
            log.info("{}: {} (Email: {}) was created via Google", LogEnum.SERVICE, OBJECT_NAME, email);
            return create(newUser);
        } else {
            UserEntity existingUser = userOptional.get();
            existingUser.setUsername(name);

            log.info("{}: {} (Email: {}) was logged in via Google", LogEnum.SERVICE, OBJECT_NAME, email);
            return userMapper.toResponse(userRepository.save(existingUser));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }


    //FIND BY
    public UserEntity findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomNotFoundException(OBJECT_NAME, id));
    }

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

    private UserEntity findByContactInfo(String contact){
        log.info("{}: searching for user by any contact info: {}", LogEnum.SERVICE, contact);

        return userRepository.findByEmail(contact)
                .or(() -> userRepository.findByGoogleAuthId(contact))
                .or(() -> userRepository.findByTelegramId(contact))
                .orElseThrow(()->new CustomNotFoundException(OBJECT_NAME, contact));
    }

    private void validateUserUpdation(UserEntity userEntity, UserRequestDto request) {
        String email = request.email();
        String googleAuthId = request.googleAuthId();
        String telegramId = request.telegramId();

        if (!email.equals(userEntity.getEmail())) {
            if (userRepository.existsByEmail(email)){
                throw new CustomAlreadyExistException(OBJECT_NAME, "Email", email);
            }
//            userEntity.setEmailVerified(false);
//            userEntity.setEmailVerificationCode(UUID.randomUUID().toString().substring(0, 6));
        }
        if (!googleAuthId.equals(userEntity.getGoogleAuthId())) {
            if (userRepository.existsByGoogleAuthId(googleAuthId)){
                throw new CustomAlreadyExistException(OBJECT_NAME, "Google Auth Id", googleAuthId);
            }
        }
        if (!telegramId.equals(userEntity.getTelegramId())) {
            if (userRepository.existsByTelegramId(telegramId)){
                throw new CustomAlreadyExistException(OBJECT_NAME, "Telegram Id", telegramId);
            }
        }
    }
}
