package tech.nilanjan.spring.backend.main.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@ConfigurationProperties("application.jwt")
public class JwtConfig {
    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfterMinutes;
    private String emailVerificationSecretKey;
    private Integer emailVerificationTokenExpirationAfterMinutes;
    private String resetPasswordSecretKey;
    private Integer resetPasswordTokenExpirationAfterMinutes;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public void setTokenExpirationAfterMinutes(Integer tokenExpirationAfterMinutes) {
        this.tokenExpirationAfterMinutes = tokenExpirationAfterMinutes;
    }

    public void setEmailVerificationSecretKey(String emailVerificationSecretKey) {
        this.emailVerificationSecretKey = emailVerificationSecretKey;
    }

    public void setEmailVerificationTokenExpirationAfterMinutes(
            Integer emailVerificationTokenExpirationAfterMinutes
    ) {
        this.emailVerificationTokenExpirationAfterMinutes = emailVerificationTokenExpirationAfterMinutes;
    }

    public void setResetPasswordSecretKey(String resetPasswordSecretKey) {
        this.resetPasswordSecretKey = resetPasswordSecretKey;
    }

    public void setResetPasswordTokenExpirationAfterMinutes(
            Integer resetPasswordTokenExpirationAfterMinutes
    ) {
        this.resetPasswordTokenExpirationAfterMinutes = resetPasswordTokenExpirationAfterMinutes;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public Integer getTokenExpirationAfterMinutes() {
        return tokenExpirationAfterMinutes;
    }

    public String getAuthorizationHeader() {
        return AUTHORIZATION;
    }

    public String getEmailVerificationSecretKey() {
        return emailVerificationSecretKey;
    }

    public Integer getEmailVerificationTokenExpirationAfterMinutes() {
        return emailVerificationTokenExpirationAfterMinutes;
    }

    public String getResetPasswordSecretKey() {
        return resetPasswordSecretKey;
    }

    public Integer getResetPasswordTokenExpirationAfterMinutes() {
        return resetPasswordTokenExpirationAfterMinutes;
    }
}
