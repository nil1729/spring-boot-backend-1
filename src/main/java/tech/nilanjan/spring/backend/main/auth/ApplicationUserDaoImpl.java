package tech.nilanjan.spring.backend.main.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;
import tech.nilanjan.spring.backend.main.io.entity.UserEntity;
import tech.nilanjan.spring.backend.main.repo.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository("auth_user_db")
public class ApplicationUserDaoImpl implements ApplicationUserDao {
    private final UserRepository userRepository;

    @Autowired
    public ApplicationUserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ApplicationUser> selectUserByEmail(String email) {
        Optional<UserEntity> userData = userRepository.findUserEntityByEmail(email);

        if (userData.isPresent()) {
            Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            return Optional.of(
                    new ApplicationUser(
                            grantedAuthorities,
                            userData.get().getPassword(),
                            userData.get().getEmail(),
                            true,
                            true,
                            true,
                            true
                    )
            );
        }

        return Optional.empty();
    }
}
