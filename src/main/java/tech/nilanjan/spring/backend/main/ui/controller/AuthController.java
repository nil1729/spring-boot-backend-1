package tech.nilanjan.spring.backend.main.ui.controller;

import com.google.common.base.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;
import tech.nilanjan.spring.backend.main.security.jwt.JwtUtil;
import tech.nilanjan.spring.backend.main.service.UserService;
import tech.nilanjan.spring.backend.main.shared.dto.UserDto;
import tech.nilanjan.spring.backend.main.ui.model.request.UserRequestDetails;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;
import tech.nilanjan.spring.backend.main.ui.model.response.UserRest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("v1/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(
            path = "sign-up",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public UserRest userSignUp(@RequestBody UserRequestDetails userDetails) {
        if (Strings.isNullOrEmpty(userDetails.getEmail())) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);

        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;
    }

    @PostMapping(
            path = "sign-in",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public ResponseEntity<Map<String, String>> userSignIn(
            @RequestBody UserRequestDetails userDetails,
            HttpServletRequest request
    ) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getEmail(),
                userDetails.getPassword()
        );
        Authentication authResult = authenticationManager.authenticate(authentication);

        String accessToken = jwtUtil.generateAccessToken(authResult, request);
        Map<String, String> result = new HashMap<>();
        result.put("access_token", accessToken);

        return ResponseEntity.ok().body(result);
    }
}
