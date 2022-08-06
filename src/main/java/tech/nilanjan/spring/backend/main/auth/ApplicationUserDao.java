package tech.nilanjan.spring.backend.main.auth;

import java.util.Optional;

public interface ApplicationUserDao {
    Optional<ApplicationUser> selectUserByEmail(String email);
}
