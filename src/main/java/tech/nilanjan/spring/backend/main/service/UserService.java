package tech.nilanjan.spring.backend.main.service;

import org.springframework.stereotype.Service;
import tech.nilanjan.spring.backend.main.shared.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto getUserByEmail(String userEmail);

    UserDto updateUser(String userEmail, UserDto userDto);

    void deleteUser(String userEmail);

    List<UserDto> getUsersList(Integer page, Integer limit);
}
