package tech.nilanjan.spring.backend.main.service;

import org.springframework.stereotype.Service;
import tech.nilanjan.spring.backend.main.shared.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);
}
