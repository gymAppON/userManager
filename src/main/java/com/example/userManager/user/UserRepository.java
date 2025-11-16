package com.example.userManager.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByGoogleId(String googleId);
    Optional<UserEntity> findByTelegramId(String facebookId);

    boolean existsByEmail(String email);
    boolean existsByGoogleAuthId(String googleAuthId);
    boolean existsByTelegramId(String telegramId);
}
