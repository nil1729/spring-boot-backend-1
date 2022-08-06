package tech.nilanjan.spring.backend.main.security.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class JwtEmailAndPasswordAuthenticationFilter
        extends UsernamePasswordAuthenticationFilter {

    private final JwtConfig jwtConfig;
    private final JwtAlgorithm jwtAlgorithm;
    private final AuthenticationManager authenticationManager;

    public JwtEmailAndPasswordAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtConfig jwtConfig,
            JwtAlgorithm jwtAlgorithm
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.jwtAlgorithm = jwtAlgorithm;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        try {
            JwtEmailAndPasswordRequest authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), JwtEmailAndPasswordRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException {
        response.setStatus(400);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Authentication failed due to bad credentials");

        response.setHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), result);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        String accessToken = JWT
                .create()
                .withSubject(authResult.getName())
                .withClaim("permissions",
                        authResult
                                .getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                ).withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60 * jwtConfig.getTokenExpirationAfterMinutes()))
                .withIssuedAt(new Date())
                .withIssuer(request.getRequestURL().toString())
                .sign(jwtAlgorithm.getAlgorithm());

        Map<String, String> result = new HashMap<>();
        result.put("access_token", accessToken);

        response.setHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), result);

    }
}
