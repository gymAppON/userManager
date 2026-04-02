package com.example.userManager.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByGoogleAuthId(String googleId);
    Optional<UserEntity> findByTelegramId(String facebookId);

    Optional<UserEntity> findByEmailVerificationCode(String emailVerificationCode);
    Optional<UserEntity> findByPasswordVerificationCode(String passwordVerificationCode);

    void setStatusNON_ACTIVE(UUID id);
    void setStatusACTIVE(UUID id);

    boolean existsByEmail(String email);
    boolean existsByGoogleAuthId(String googleAuthId);
    boolean existsByTelegramId(String telegramId);
}
