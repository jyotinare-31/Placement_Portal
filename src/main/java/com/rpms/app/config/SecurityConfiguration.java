package com.rpms.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpms.app.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {
  @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

  @Bean
  UserDetailsService userDetailsService(UserRepository users) {
    return username -> users.findByUsername(username)
        .map(account -> User.withUsername(account.getUsername())
            .password(account.getPasswordHash())
            .roles(account.getRole().getRoleCode())
            .disabled(!"ACTIVE".equals(account.getStatus()) || !Boolean.TRUE.equals(account.getRole().getActive()))
            .accountLocked("LOCKED".equals(account.getStatus()))
            .build())
        .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Invalid username or password"));
  }

  @Bean
  SecurityContextRepository securityContextRepository() { return new HttpSessionSecurityContextRepository(); }

  @Bean
  CsrfTokenRepository csrfTokenRepository(org.springframework.core.env.Environment environment) {
    CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    repository.setCookiePath("/");
    boolean secure = environment.getProperty("COOKIE_SECURE", Boolean.class, false);
    repository.setCookieCustomizer(cookie -> cookie.sameSite("Lax").secure(secure));
    return repository;
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper, SecurityContextRepository securityContextRepository, CsrfTokenRepository csrfRepository) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(csrfRepository)
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            .ignoringRequestMatchers("/api/v1/auth/login", "/api/v1/auth/logout"))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/csrf", "/api/v1/auth/login").permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .logout(logout -> logout.disable())
        .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint((request, response, exception) -> {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          objectMapper.writeValue(response.getOutputStream(), Map.of("message", "Sign in to continue"));
        }))
        .sessionManagement(session -> session.sessionFixation(fixation -> fixation.migrateSession()))
        .securityContext(context -> context.securityContextRepository(securityContextRepository));
    return http.build();
  }
}
