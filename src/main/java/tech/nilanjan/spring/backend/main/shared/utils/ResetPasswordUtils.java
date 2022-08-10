package tech.nilanjan.spring.backend.main.shared.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.nilanjan.spring.backend.main.security.jwt.JwtAlgorithm;
import tech.nilanjan.spring.backend.main.security.jwt.JwtConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class ResetPasswordUtils {
    private final JwtConfig jwtConfig;
    private final JwtAlgorithm jwtAlgorithm;

    @Autowired
    public ResetPasswordUtils(JwtConfig jwtConfig, JwtAlgorithm jwtAlgorithm) {
        this.jwtConfig = jwtConfig;
        this.jwtAlgorithm = jwtAlgorithm;
    }

    public Boolean checkIsTokenExpired(String verificationToken) {
        try {
            JWTVerifier jwtVerifier = JWT.require(jwtAlgorithm.getResetPasswordAlgorithm()).build();
            jwtVerifier.verify(verificationToken);
            return false;
        } catch (JWTVerificationException ex) {
            return true;
        }
    }

    public String generatePasswordResetToken(String userId, HttpServletRequest request) {
        return JWT
                .create()
                .withSubject(userId)
                .withExpiresAt(new Date(
                        System.currentTimeMillis() +
                                1000L * 60 * jwtConfig.getResetPasswordTokenExpirationAfterMinutes()
                ))
                .withIssuedAt(new Date())
                .withIssuer(request.getRequestURL().toString())
                .sign(jwtAlgorithm.getResetPasswordAlgorithm());
    }
}
