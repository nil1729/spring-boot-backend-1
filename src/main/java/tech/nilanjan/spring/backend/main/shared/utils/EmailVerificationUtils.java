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
public class EmailVerificationUtils {
    private final JwtAlgorithm jwtAlgorithm;
    private final JwtConfig jwtConfig;

    @Autowired
    public EmailVerificationUtils(JwtAlgorithm jwtAlgorithm, JwtConfig jwtConfig) {
        this.jwtAlgorithm = jwtAlgorithm;
        this.jwtConfig = jwtConfig;
    }

    public Boolean checkIsTokenExpired(String verificationToken) {
        try {
            JWTVerifier jwtVerifier = JWT.require(jwtAlgorithm.getEmailVerificationAlgorithm()).build();
            jwtVerifier.verify(verificationToken);
            return false;
        } catch (JWTVerificationException ex) {
            return true;
        }
    }

    public String generateVerificationToken(String userId, HttpServletRequest request) {
        return JWT
                .create()
                .withSubject(userId)
                .withExpiresAt(new Date(
                        System.currentTimeMillis() +
                                1000L * 60 * jwtConfig.getEmailVerificationTokenExpirationAfterMinutes()
                ))
                .withIssuedAt(new Date())
                .withIssuer(request.getRequestURL().toString())
                .sign(jwtAlgorithm.getEmailVerificationAlgorithm());
    }
}
