package tech.nilanjan.spring.backend.main.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.nilanjan.spring.backend.main.service.UserService;
import tech.nilanjan.spring.backend.main.shared.dto.UserDto;
import tech.nilanjan.spring.backend.main.ui.model.response.UserRest;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserRest>> getUsersList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "25") Integer limit
    ) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> usersList = userService.getUsersList(page, limit);

        for (UserDto userDto: usersList) {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto, userRest);
            returnValue.add(userRest);
        }

        return ResponseEntity.ok().body(returnValue);
    }
}
