package tech.nilanjan.spring.backend.main.ui.controller;

import com.google.common.base.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;
import tech.nilanjan.spring.backend.main.service.UserService;
import tech.nilanjan.spring.backend.main.shared.dto.UserDto;
import tech.nilanjan.spring.backend.main.ui.model.request.UserRequestDetails;
import tech.nilanjan.spring.backend.main.ui.model.response.OperationStatusRest;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;
import tech.nilanjan.spring.backend.main.ui.model.response.UserRest;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.OperationNames;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.OperationStatus;


@RestController
@RequestMapping("/v1/profile")
public class UserProfileController {
    private final UserService userService;

    @Autowired
    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserRest> getUserProfile(Authentication authResult) {
        String userEmail = authResult.getName();
        UserDto userDetails = userService.getUserByEmail(userEmail);

        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(userDetails, returnValue);

        return ResponseEntity.ok().body(returnValue);
    }

    @PutMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserRest> updateUserProfile(
            Authentication authResult,
            @RequestBody UserRequestDetails userDetails
    ) {
        String userEmail = authResult.getName();

        if(Strings.isNullOrEmpty(userDetails.getFirstName()) ||
                Strings.isNullOrEmpty(userDetails.getLastName())) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(userEmail, userDto);

        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(updatedUser, returnValue);

        return ResponseEntity.ok().body(returnValue);
    }

    @DeleteMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public ResponseEntity<OperationStatusRest> deleteProfile(Authentication authResult) {
        OperationStatusRest operationStatusRest = new OperationStatusRest();
        operationStatusRest.setOperationName(OperationNames.DELETE.name());

        String userEmail = authResult.getName();
        userService.deleteUser(userEmail);

        operationStatusRest.setOperationStatus(OperationStatus.SUCCESS.name());
        return ResponseEntity.ok().body(operationStatusRest);
    }
}
