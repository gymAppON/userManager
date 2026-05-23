package com.example.userManager.domain.user;

import com.example.userManager.domain.auth.dto.SignupRequestDto;
import com.example.userManager.domain.user.dto.UserRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;
import com.example.userManager.infrastructure.config.mail.notifications.UserSecurityCodeUpdatedEvent;
import com.example.userManager.shared.enums.EntityStatus;
import com.example.userManager.shared.enums.LanguageEnum;
import com.example.userManager.shared.enums.WeightUnitEnum;
import com.example.userManager.shared.exception.exceptions.general.CustomAlreadyExistException;
import com.example.userManager.shared.exception.exceptions.general.CustomNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private UserMapper userMapper = new UserMapperImpl(); // Используем реальную реализацию, если она не зависит от других компонентов

    @InjectMocks
    private UserServiceImpl userService;

    private SignupRequestDto signupRequest;
    private UserEntity userEntity;
    private UserResponseDto userResponseDto;
    private UserMetadata userMetadata;

    @BeforeEach
    void setUp() {
        // Чиним инъекцию зависимости сеттера
        userService.passwordEncoder(passwordEncoder);

        // Инициализация общих тестовых данных
        // Предполагается, что UserMetadata есть в твоем проекте
        userMetadata = new UserMetadata(WeightUnitEnum.KG, LanguageEnum.EN, "UTC");

        signupRequest = new SignupRequestDto(
                "testuser", "test@example.com", null, null, "password123", userMetadata
        );

        userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setEmailVerified(false);
        userEntity.setPasswordVerified(true);
        userEntity.setStatus(EntityStatus.ACTIVE);
        userEntity.setMetadata(userMetadata); // Раскомментируй, если у Entity есть этот метод

        userResponseDto = userMapper.toResponse(userEntity);
    }

    @Test
    void create_ShouldCreateUserSuccessfully_WhenEmailDoesNotExist() {
        // Arrange
        when(userRepository.existsByEmail(signupRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        UserResponseDto result = userService.create(signupRequest);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDto.id(), result.id());
        assertEquals(userResponseDto.email(), result.email());

        // Verifications
        verify(userRepository).existsByEmail(signupRequest.email());
        verify(userMapper).toEntity(signupRequest);
        verify(passwordEncoder).encode(signupRequest.password());
        verify(userRepository).save(any(UserEntity.class));
        verify(eventPublisher).publishEvent(any(UserSecurityCodeUpdatedEvent.class));
        verify(userMapper, atLeast(1)).toResponse(userEntity);
    }

    @Test
    void create_ShouldThrowCustomAlreadyExistException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(signupRequest.email())).thenReturn(true);

        // Act & Assert
        // Проверяем только то, что исключение нужного типа выбрасывается
        assertThrows(
                CustomAlreadyExistException.class,
                () -> userService.create(signupRequest)
        );

        // Проверяем, что поиск по email был
        verify(userRepository).existsByEmail(signupRequest.email());

        // Гарантируем, что после ошибки логика прервалась и сохранения не было
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

//    @Test
//    void delete_ShouldDeleteUserSuccessfully_WhenUserExist(){
//        //Act
//        userService.delete(userEntity.getId());
//
//        //Verify
//        verify(userRepository).updateStatus(userEntity.getId(), EntityStatus.NON_ACTIVE);
//    }
//
//    @Test
//    void delete_ShouldThrowException_WhenUserDoesNotExist(){
//        //Arrange
//        UUID incorrectID = UUID.randomUUID();
//        doThrow(new RuntimeException()).when(userRepository).updateStatus(incorrectID, EntityStatus.NON_ACTIVE);
//
//
//        //Act
//        assertThrows(RuntimeException.class,
//                () -> userService.delete(incorrectID)
//                );
//
//        //Verify
//        verify(userRepository).updateStatus(any(UUID.class), EntityStatus.NON_ACTIVE);
//    }

    @Test
    void getById_ShouldReturnUserSuccessfully_WhenUserExistAndActive(){
        //Arrange
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

        //Act
        UserResponseDto actual = userService.getById(userEntity.getId());
        assertEquals(userResponseDto,  actual);

        //Verify
        verify(userRepository).findById(userEntity.getId());
        verify(userMapper, atLeast(1)).toResponse(userEntity);
    }

    @Test
    void getById_ShouldThrowException_WhenUserDoesNotExist(){
        //Arrange
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

        //Act
        assertThrows(CustomNotFoundException.class,
                () -> userService.getById(userEntity.getId())
                );

        //Verify
        verify(userRepository).findById(userEntity.getId());
        //verify(userMapper).toResponse(userEntity);
    }

    @Test
    void getById_ShouldThrowException_WhenUserExistButNotActive(){
        //Arrange
        userEntity.setStatus(EntityStatus.NON_ACTIVE);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

        //Act
        assertThrows(CustomNotFoundException.class,
                () -> userService.getById(userEntity.getId())
        );

        //Verify
        verify(userRepository).findById(userEntity.getId());
    }

    @Test
    void update_ShouldUpdateEmailAndPublishEvent_WhenOnlyEmailIsChanged() {
        // --- 1. ARRANGE ---
        // Сохраняем "старый" email для проверки
        String oldEmail = userEntity.getEmail();
        String newEmail = "newemali@mail.com";

        // Создаем запрос с НОВЫМ email, но СТАРЫМИ остальными данными
        UserRequestDto updateRequest = new UserRequestDto(
                userEntity.getUsername(),
                newEmail,
                userEntity.getTelegramId(),
                userEntity.getGoogleAuthId(),
                "password123",
                userEntity.getMetadata()
        );

        // Мокаем базу: возвращаем юзера со СТАРЫМ email
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

        // Мокаем проверки (email свободен, пароль совпадает)
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(passwordEncoder.matches(updateRequest.password(), userEntity.getPassword())).thenReturn(true);

        // При сохранении возвращаем ту же сущность (маппер/сервис сами обновят в ней поля)
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- 2. ACT ---
        UserResponseDto actual = userService.update(userEntity.getId(), updateRequest);

        // --- 3. ASSERT (Проверяем DTO) ---
        assertEquals(newEmail, actual.email());

        // --- 4. VERIFY & CAPTURE (Шпионаж за внутренними объектами) ---
        verify(userRepository).findById(userEntity.getId());
        verify(userRepository).existsByEmail(newEmail);

        // Создаем ловушку для UserEntity
        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);

        // Проверяем, что save вызывался, и ОДНОВРЕМЕННО ловим то, что в него передали
        verify(userRepository).save(entityCaptor.capture());

        // Вытаскиваем пойманный объект (это и есть твой fromRequest)
        UserEntity capturedEntityToSave = entityCaptor.getValue();

        // ВОТ ТЕПЕРЬ проверяем внутреннюю логику!
        assertNotNull(capturedEntityToSave.getEmailVerificationCode()); // UUID сгенерировался!
        assertFalse(capturedEntityToSave.isEmailVerified());
        assertEquals(newEmail, capturedEntityToSave.getEmail());

        // Проверка отправки события
        verify(eventPublisher).publishEvent(any(UserSecurityCodeUpdatedEvent.class));
        // Проверяем, что НЕ нужные методы НЕ вызвались (это гарантия изоляции if-блоков)
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).existsByGoogleAuthId(anyString());
        verify(userRepository, never()).existsByTelegramId(anyString());
    }
}