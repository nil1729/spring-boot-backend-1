package tech.nilanjan.spring.backend.main.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class JwtTokenVerifier
        extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final JwtAlgorithm jwtAlgorithm;

    @Autowired
    public JwtTokenVerifier(JwtConfig jwtConfig, JwtAlgorithm jwtAlgorithm) {
        this.jwtConfig = jwtConfig;
        this.jwtAlgorithm = jwtAlgorithm;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());

        if(Strings.isNullOrEmpty(authorizationHeader) ||
                !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
            JWTVerifier jwtVerifier = JWT.require(jwtAlgorithm.getAlgorithm()).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(accessToken);

            String username = decodedJWT.getSubject();
            List<String> permissions = decodedJWT.getClaim("permissions").asList(String.class);
            List<SimpleGrantedAuthority> grantedAuthorities = permissions
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    grantedAuthorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
