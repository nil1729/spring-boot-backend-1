package tech.nilanjan.spring.backend.main.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAlgorithm {
    private final JwtConfig jwtConfig;

    @Autowired
    public JwtAlgorithm(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtConfig.getSecretKey().getBytes());
    }
}
