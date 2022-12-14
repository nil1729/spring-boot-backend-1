package tech.nilanjan.spring.backend.main.ui.controller;

import com.google.common.base.Strings;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
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
import tech.nilanjan.spring.backend.main.ui.model.response.LoginRest;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;
import tech.nilanjan.spring.backend.main.ui.model.response.UserRest;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/auth")
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
    @ApiOperation(
            value = "Sign Up web service endpoint",
            notes = "This web service endpoints returns registered User Details"
    )
    public ResponseEntity<UserRest> userSignUp(
            @RequestBody UserRequestDetails userDetails,
            HttpServletRequest request
    ) {
        if (Strings.isNullOrEmpty(userDetails.getEmail())) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto, request);

        UserRest returnValue = modelMapper.map(createdUser, UserRest.class);

        return ResponseEntity.ok().body(returnValue);
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
    public ResponseEntity<LoginRest> userSignIn(
            @RequestBody UserRequestDetails userDetails,
            HttpServletRequest request
    ) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getEmail(),
                userDetails.getPassword()
        );
        Authentication authResult = authenticationManager.authenticate(authentication);

        String accessToken = jwtUtil.generateAccessToken(authResult, request);
        LoginRest returnValue = new LoginRest(accessToken);

        return ResponseEntity.ok().body(returnValue);
    }
}
