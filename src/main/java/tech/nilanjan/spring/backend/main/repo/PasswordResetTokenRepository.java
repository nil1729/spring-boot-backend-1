package tech.nilanjan.spring.backend.main.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.nilanjan.spring.backend.main.io.entity.PasswordResetTokenEntity;
import tech.nilanjan.spring.backend.main.io.entity.UserEntity;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findPasswordResetTokenEntitiesByUserDetails(UserEntity userEntity);
    Optional<PasswordResetTokenEntity> findPasswordResetTokenEntitiesByToken(String token);
}
