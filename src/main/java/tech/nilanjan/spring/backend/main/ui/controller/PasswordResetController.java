package tech.nilanjan.spring.backend.main.ui.controller;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;
import tech.nilanjan.spring.backend.main.service.UserService;
import tech.nilanjan.spring.backend.main.ui.model.request.PasswordResetDetails;
import tech.nilanjan.spring.backend.main.ui.model.request.PasswordResetRequest;
import tech.nilanjan.spring.backend.main.ui.model.response.OperationStatusRest;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.OperationNames;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.OperationStatus;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/password-reset")
public class PasswordResetController {
    private final UserService userService;

    @Autowired
    public PasswordResetController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
            path = "/request",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public ResponseEntity<OperationStatusRest> passwordResetRequest(
            @RequestBody PasswordResetRequest passwordResetRequestDetails, HttpServletRequest request
    ) {
        if (Strings.isNullOrEmpty(passwordResetRequestDetails.getEmail())) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        OperationStatusRest returnValue = new OperationStatusRest();
        returnValue.setOperationName(OperationNames.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationStatus(OperationStatus.ERROR.name());

        Boolean isSuccess = userService.resetPasswordRequest(passwordResetRequestDetails.getEmail(), request);

        if (isSuccess) {
            returnValue.setOperationStatus(OperationStatus.SUCCESS.name());
        }

        return ResponseEntity.ok().body(returnValue);
    }

    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public ResponseEntity<OperationStatusRest> resetPassword(
            @RequestBody PasswordResetDetails passwordResetDetails
    ) {
        if (Strings.isNullOrEmpty(passwordResetDetails.getPassword()) ||
                Strings.isNullOrEmpty(passwordResetDetails.getToken())
        ) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        OperationStatusRest returnValue = new OperationStatusRest();
        returnValue.setOperationName(OperationNames.RESET_PASSWORD.name());
        returnValue.setOperationStatus(OperationStatus.ERROR.name());

        Boolean isSuccess = userService.resetPassword(
                passwordResetDetails.getPassword(),
                passwordResetDetails.getToken()
        );

        if (isSuccess) {
            returnValue.setOperationStatus(OperationStatus.SUCCESS.name());
        }

        return ResponseEntity.ok().body(returnValue);
    }
}
