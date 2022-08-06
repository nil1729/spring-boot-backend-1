package tech.nilanjan.spring.backend.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.nilanjan.spring.backend.main.auth.ApplicationUserDetailsService;
import tech.nilanjan.spring.backend.main.security.jwt.JwtAlgorithm;
import tech.nilanjan.spring.backend.main.security.jwt.JwtConfig;
import tech.nilanjan.spring.backend.main.security.jwt.JwtEmailAndPasswordAuthenticationFilter;
import tech.nilanjan.spring.backend.main.security.jwt.JwtTokenVerifier;

@Configuration
public class ApplicationSecurityConfig
        extends WebSecurityConfigurerAdapter {

    private final JwtConfig jwtConfig;
    private final JwtAlgorithm jwtAlgorithm;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserDetailsService applicationUserDetailsService;

    @Autowired
    public ApplicationSecurityConfig(
            JwtConfig jwtConfig,
            JwtAlgorithm jwtAlgorithm,
            PasswordEncoder passwordEncoder,
            ApplicationUserDetailsService applicationUserDetailsService
    ) {
        this.jwtConfig = jwtConfig;
        this.jwtAlgorithm = jwtAlgorithm;
        this.passwordEncoder = passwordEncoder;
        this.applicationUserDetailsService = applicationUserDetailsService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtEmailAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, jwtAlgorithm))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig, jwtAlgorithm), JwtEmailAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/v1/auth/**").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserDetailsService);
        return provider;
    }
}
