package tech.nilanjan.spring.backend.main.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;

import java.util.Optional;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final ApplicationUserDao applicationUserDao;

    @Autowired
    public ApplicationUserDetailsService(@Qualifier(value = "auth_user_db") ApplicationUserDao applicationUserDao) {
        this.applicationUserDao = applicationUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UserServiceException {

        return applicationUserDao
                .selectUserByEmail(email)
                .orElseThrow(() ->
                        new UserServiceException(
                                String.format("Email address [%s] not registered with us", email)
                        )
                );
    }
}
