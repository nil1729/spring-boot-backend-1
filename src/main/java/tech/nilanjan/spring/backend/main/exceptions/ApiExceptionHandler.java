package tech.nilanjan.spring.backend.main.exceptions;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.nilanjan.spring.backend.main.ui.model.response.ErrorRest;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {UserServiceException.class})
    public ResponseEntity<Object> handleUserServiceException(UserServiceException ex) {
        ErrorRest errorRest = new ErrorRest(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(errorRest, errorRest.getHttpStatus());
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherException(Exception ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Class<? extends Exception> exceptionClass = ex.getClass();

        if(HttpRequestMethodNotSupportedException.class.equals(exceptionClass)) {
            httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        } else if(BadCredentialsException.class.equals(exceptionClass)) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (JWTDecodeException.class.equals(exceptionClass)) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if(
                AccessDeniedException.class.equals(exceptionClass) ||
                        InternalAuthenticationServiceException.class.equals(exceptionClass) ||
                        DisabledException.class.equals(exceptionClass)
        ) {
            httpStatus = HttpStatus.FORBIDDEN;
        }

        ErrorRest errorRest = new ErrorRest(
                ex.getMessage(),
                httpStatus,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(errorRest, errorRest.getHttpStatus());
    }

}
