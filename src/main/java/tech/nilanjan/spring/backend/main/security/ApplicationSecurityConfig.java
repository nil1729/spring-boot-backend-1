package tech.nilanjan.spring.backend.main.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import tech.nilanjan.spring.backend.main.auth.ApplicationUserDetailsService;
import tech.nilanjan.spring.backend.main.security.constant.PublicRoutes;
import tech.nilanjan.spring.backend.main.security.jwt.JwtAlgorithm;
import tech.nilanjan.spring.backend.main.security.jwt.JwtConfig;
import tech.nilanjan.spring.backend.main.security.jwt.JwtTokenVerifier;
import tech.nilanjan.spring.backend.main.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig
        extends WebSecurityConfigurerAdapter {

    private final JwtConfig jwtConfig;
    private final JwtAlgorithm jwtAlgorithm;
    private final ApplicationUserDetailsService applicationUserDetailsService;
    private final FilterChainExceptionHandler filterChainExceptionHandler;
    private final UserService userService;

    @Autowired
    public ApplicationSecurityConfig(
            JwtConfig jwtConfig,
            JwtAlgorithm jwtAlgorithm,
            ApplicationUserDetailsService applicationUserDetailsService,
            FilterChainExceptionHandler filterChainExceptionHandler,
            UserService userService
    ) {
        this.jwtConfig = jwtConfig;
        this.jwtAlgorithm = jwtAlgorithm;
        this.applicationUserDetailsService = applicationUserDetailsService;
        this.filterChainExceptionHandler = filterChainExceptionHandler;
        this.userService = userService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic().disable()
                .cors()
                .and()
                .authorizeHttpRequests()
                .antMatchers(
                        PublicRoutes.AUTH_ROUTE.getHttpMethod(),
                        PublicRoutes.AUTH_ROUTE.getRoute()
                    ).permitAll()
                .antMatchers(
                        PublicRoutes.EMAIL_VERIFICATION_ROUTE.getHttpMethod(),
                        PublicRoutes.EMAIL_VERIFICATION_ROUTE.getRoute()
                    ).permitAll()
                .antMatchers(
                        PublicRoutes.PASSWORD_RESET_REQUEST.getHttpMethod(),
                        PublicRoutes.PASSWORD_RESET_REQUEST.getRoute()
                    ).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .userDetailsService(applicationUserDetailsService)
                .exceptionHandling()
                    .accessDeniedHandler(
                            (request, response, accessDeniedException) -> {
                                Map<String, String> result = new HashMap<>();
                                result.put("message", "You are unauthorized to access the resource");

                                response.setStatus(401);
                                response.setHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                                new ObjectMapper().writeValue(response.getOutputStream(), result);
                            }
                    )
                    .authenticationEntryPoint(
                        (request, response, authenticationException) -> {
                            Map<String, String> result = new HashMap<>();
                            result.put("message", "Authentication failed due to bad credentials");

                            response.setStatus(400);
                            response.setHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                            new ObjectMapper().writeValue(response.getOutputStream(), result);
                        }
                    )
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(filterChainExceptionHandler, LogoutFilter.class);
        http.addFilterBefore(new JwtTokenVerifier(jwtConfig, jwtAlgorithm, userService), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
