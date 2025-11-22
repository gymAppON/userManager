# Рекомендована структура User Service

## 📁 Повна структура проекту

```
user-service/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── userManager/
│   │   │               │
│   │   │               ├── UserManagerApplication.java
│   │   │               │
│   │   │               ├── domain/                    # 🎯 БІЗНЕС ЛОГІКА
│   │   │               │   │
│   │   │               │   ├── user/
│   │   │               │   │   ├── UserController.java
│   │   │               │   │   ├── UserService.java
│   │   │               │   │   ├── UserServiceImpl.java
│   │   │               │   │   ├── UserRepository.java
│   │   │               │   │   ├── UserMapper.java
│   │   │               │   │   ├── dto/
│   │   │               │   │   │   ├── CreateUserRequest.java
│   │   │               │   │   │   ├── UpdateUserRequest.java
│   │   │               │   │   │   ├── UserResponse.java
│   │   │               │   │   │   └── UserDetailResponse.java
│   │   │               │   │   ├── UserCreatedEvent.java
│   │   │               │   │   ├── UserUpdatedEvent.java
│   │   │               │   │   └── UserDeletedEvent.java
│   │   │               │   │
│   │   │               │   ├── auth/
│   │   │               │   │   ├── AuthController.java
│   │   │               │   │   ├── AuthService.java
│   │   │               │   │   ├── AuthServiceImpl.java
│   │   │               │   │   └── dto/
│   │   │               │   │       ├── LoginRequest.java
│   │   │               │   │       ├── SignupRequest.java
│   │   │               │   │       └── AuthResponse.java
│   │   │               │   │
│   │   │               │   └── profile/                    # Майбутній feature
│   │   │               │       ├── ProfileController.java
│   │   │               │       ├── ProfileService.java
│   │   │               │       ├── ProfileServiceImpl.java
│   │   │               │       └── dto/
│   │   │               │           └── ...
│   │   │               │
│   │   │               ├── entity/                    # 🗄️ JPA ENTITIES (ОКРЕМО!)
│   │   │               │   ├── BaseEntity.java
│   │   │               │   ├── User.java
│   │   │               │   ├── UserMetadata.java
│   │   │               │   ├── AuditLog.java
│   │   │               │   └── RefreshToken.java      # Якщо потрібно
│   │   │               │
│   │   │               ├── infrastructure/            # ⚙️ ТЕХНІЧНІ РЕЧІ
│   │   │               │   │
│   │   │               │   ├── security/
│   │   │               │   │   ├── SecurityConfig.java
│   │   │               │   │   ├── UserDetailsServiceImpl.java
│   │   │               │   │   ├── OAuth2LoginSuccessHandler.java
│   │   │               │   │   └── filter/
│   │   │               │   │       └── RequestLoggingFilter.java
│   │   │               │   │
│   │   │               │   ├── config/
│   │   │               │   │   ├── OpenAPIConfig.java
│   │   │               │   │   ├── MapperConfig.java
│   │   │               │   │   ├── WebConfig.java
│   │   │               │   │   ├── JpaConfig.java
│   │   │               │   │   └── properties/
│   │   │               │   │       ├── AppProperties.java
│   │   │               │   │       └── OAuth2Properties.java
│   │   │               │   │
│   │   │               │   ├── persistence/
│   │   │               │   │   ├── audit/
│   │   │               │   │   │   ├── AuditLogRepository.java
│   │   │               │   │   │   └── AuditLogService.java
│   │   │               │   │   └── listener/
│   │   │               │   │       └── AuditingEntityListener.java
│   │   │               │   │
│   │   │               │   └── messaging/              # Kafka/RabbitMQ
│   │   │               │       ├── EventPublisher.java
│   │   │               │       └── config/
│   │   │               │           └── KafkaConfig.java
│   │   │               │
│   │   │               └── shared/                    # 🔧 СПІЛЬНІ УТИЛІТИ
│   │   │                   │
│   │   │                   ├── exception/
│   │   │                   │   ├── GlobalExceptionHandler.java
│   │   │                   │   ├── handler/
│   │   │                   │   │   └── RestExceptionHandler.java
│   │   │                   │   └── types/
│   │   │                   │       ├── CustomNotFoundException.java
│   │   │                   │       ├── CustomBadRequestException.java
│   │   │                   │       ├── CustomAlreadyExistException.java
│   │   │                   │       ├── UnauthorizedAccessException.java
│   │   │                   │       └── CustomErrorResponse.java
│   │   │                   │
│   │   │                   ├── enums/
│   │   │                   │   ├── Role.java
│   │   │                   │   ├── WeightUnitEnum.java
│   │   │                   │   ├── LanguageEnum.java
│   │   │                   │   └── LogEnum.java
│   │   │                   │
│   │   │                   ├── validation/
│   │   │                   │   ├── groups/
│   │   │                   │   │   ├── OnCreate.java
│   │   │                   │   │   └── OnUpdate.java
│   │   │                   │   └── validator/
│   │   │                   │       ├── UniqueEmail.java
│   │   │                   │       └── UniqueEmailValidator.java
│   │   │                   │
│   │   │                   ├── constants/
│   │   │                   │   └── AppConstants.java
│   │   │                   │
│   │   │                   └── util/
│   │   │                       ├── DateUtils.java
│   │   │                       └── StringUtils.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/
│   │       │   └── migration/                     # Flyway migrations
│   │       │       ├── V1__create_users_table.sql
│   │       │       ├── V2__add_role_to_users.sql
│   │       │       └── V3__create_audit_log.sql
│   │       └── messages/
│   │           ├── messages.properties
│   │           ├── messages_en.properties
│   │           └── messages_uk.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── userManager/
│                       ├── domain/
│                       │   ├── user/
│                       │   │   ├── UserServiceTest.java
│                       │   │   ├── UserControllerTest.java
│                       │   │   ├── UserRepositoryTest.java
│                       │   │   └── UserMapperTest.java
│                       │   └── auth/
│                       │       ├── AuthServiceTest.java
│                       │       └── AuthControllerTest.java
│                       └── integration/
│                           ├── UserIntegrationTest.java
│                           └── AuthIntegrationTest.java
│
├── .env.example
├── .gitignore
├── README.md
├── Dockerfile
├── docker-compose.yml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 📋 Пояснення структури

### 1️⃣ **`domain/`** - Бізнес логіка (Package-by-Feature)

- Кожен feature (user, auth, profile) в окремому пакеті
- Всередині feature: Controller, Service, ServiceImpl, Repository, Mapper, Events - всі файли .java
- Тільки `dto/` - окрема папка з request/response класами
- **Переваги:**
  - Легко знайти весь код для конкретного feature
  - Всі файли feature на одному рівні (без вкладених папок)
  - Масштабується при додаванні нових features
  - Легко виділити в окремий мікросервіс

### 2️⃣ **`entity/`** - JPA Entities (ОКРЕМО!)

- Всі entity моделі в одному місці
- `BaseEntity.java` - базовий клас з id, createdAt, updatedAt
- **Чому окремо:**
  - Entity використовуються в різних доменах
  - Зручно для міграцій БД
  - Чітке розділення: entity ≠ domain model

### 3️⃣ **`infrastructure/`** - Технічна інфраструктура

- Security конфігурація
- Config класи
- Messaging (Kafka/RabbitMQ)
- Persistence утиліти
- **Не бізнес логіка!**

### 4️⃣ **`shared/`** - Спільні утиліти

- Exception handling
- Enums
- Constants
- Validation
- Utilities
- **Використовуються всіма доменами**

---

## 🔄 Міграція з поточної структури

### Крок 1: Створити нову структуру

```bash
mkdir -p src/main/java/com/example/userManager/entity
mkdir -p src/main/java/com/example/userManager/domain/user/dto
mkdir -p src/main/java/com/example/userManager/domain/auth/dto
mkdir -p src/main/java/com/example/userManager/infrastructure/{security,config,persistence}
mkdir -p src/main/java/com/example/userManager/shared/{exception/types,enums,validation,constants}
```

### Крок 2: Перемістити файли

**Entities:**

```
user/UserEntity.java → entity/User.java
user/UserMetadata.java → entity/UserMetadata.java
```

**User Domain:**

```
controller/UserController.java → domain/user/UserController.java
user/UserService.java → domain/user/UserService.java
user/UserServiceImpl.java → domain/user/UserServiceImpl.java
user/UserRepository.java → domain/user/UserRepository.java
user/UserMapper.java → domain/user/UserMapper.java
dto/request/UserRequestDto.java → domain/user/dto/UpdateUserRequest.java
dto/response/UserResponseDto.java → domain/user/dto/UserResponse.java
```

**Auth Domain:**

```
controller/AuthController.java → domain/auth/AuthController.java
(створити) → domain/auth/AuthService.java
(створити) → domain/auth/AuthServiceImpl.java
dto/request/auth/LoginRequestDto.java → domain/auth/dto/LoginRequest.java
dto/request/auth/SignupRequestDto.java → domain/auth/dto/SignupRequest.java
```

**Infrastructure:**

```
security/* → infrastructure/security/
config/* → infrastructure/config/
```

**Shared:**

```
exception/* → shared/exception/
enums/* → shared/enums/
```

---

## 📝 Приклади файлів

### `entity/BaseEntity.java`

```java
package com.example.userManager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
```

### `entity/User.java`

```java
package com.example.userManager.entity;

import com.example.userManager.shared.enums.Role;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String username;

    @Column(unique = true)
    private String telegramId;

    @Column(unique = true)
    private String googleAuthId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private UserMetadata metadata;
}
```

### `domain/user/UserService.java`

```java
package com.example.userManager.domain.user;

import com.example.userManager.domain.user.dto.CreateUserRequest;
import com.example.userManager.domain.user.dto.UpdateUserRequest;
import com.example.userManager.domain.user.dto.UserResponse;
import com.example.userManager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface UserService {
    UserResponse create(CreateUserRequest request);
    UserResponse getById(UUID id);
    Page<UserResponse> getAll(Pageable pageable);
    UserResponse update(UUID id, UpdateUserRequest request);
    void delete(UUID id);

    // Internal methods
    User findByEmail(String email);
    User findById(UUID id);
}
```

### `domain/auth/AuthService.java`

```java
package com.example.userManager.domain.auth;

import com.example.userManager.domain.auth.dto.*;
import com.example.userManager.domain.user.dto.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    UserResponse register(SignupRequest request);
    UserResponse loginViaGoogle(String name, String email, String googleId);
    void logout(String userId);
}
```

### `shared/enums/Role.java`

```java
package com.example.userManager.shared.enums;

public enum Role {
    USER,
    ADMIN,
    TRAINER,
    PREMIUM_USER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
```

---

## ✅ Переваги цієї структури

1. **Чіткий розділ відповідальностей**

   - Entities - тільки дані
   - Domain - бізнес логіка
   - Infrastructure - технічні речі
   - Shared - утиліти

2. **Масштабованість**

   - Легко додати новий feature (profile, workout, etc.)
   - Легко знайти код для конкретного feature

3. **Тестованість**

   - Чітко видно що тестувати
   - Легко мокати залежності

4. **Підтримка**

   - Новий розробник швидко розбереться
   - Стандартна структура

5. **Мікросервіси**
   - Легко виділити domain в окремий сервіс
   - Мінімальні залежності між доменами

---

## 🎯 Пріоритет дій

1. ✅ Створити `entity/BaseEntity.java`
2. ✅ Перемістити `UserEntity` → `entity/User.java`
3. ✅ Додати `Role` enum
4. ✅ Створити структуру `domain/user/*`
5. ✅ Розділити UserService на UserService + AuthService
6. ✅ Перемістити файли згідно нової структури
7. ✅ Оновити імпорти
8. ✅ Запустити тести
