package tech.nilanjan.spring.backend.main.ui.controller;

import com.google.common.base.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;
import tech.nilanjan.spring.backend.main.service.UserService;
import tech.nilanjan.spring.backend.main.ui.model.response.OperationStatusRest;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.OperationNames;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.OperationStatus;

@RestController
@RequestMapping("/v1/email-verification")
public class EmailVerificationController {
    private final UserService userService;

    public EmailVerificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<OperationStatusRest> verifyEmailToken(
            @RequestParam(
                    value = "emailVerificationToken",
                    required = true
            ) String emailVerificationToken
    ) {
        if(Strings.isNullOrEmpty(emailVerificationToken)) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_NOT_VERIFIED.getErrorMessage());
        }

        OperationStatusRest returnValue = new OperationStatusRest();
        returnValue.setOperationName(OperationNames.VERIFY_EMAIL.name());

        Boolean isVerified = userService.verifyEmailAddress(emailVerificationToken);

        if(isVerified) {
            returnValue.setOperationStatus(OperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationStatus(OperationStatus.ERROR.name());
        }

        return ResponseEntity.ok().body(returnValue);
    }
}
