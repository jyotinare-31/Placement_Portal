package com.rpms.app.api;

import com.rpms.app.domain.UserEntity;
import com.rpms.app.dto.ApiRequests.LoginRequest;
import com.rpms.app.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final SecurityContextRepository securityContextRepository;
  private final CsrfTokenRepository csrfTokenRepository;
  private final UserRepository users;

  public AuthController(AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository, CsrfTokenRepository csrfTokenRepository, UserRepository users) {
    this.authenticationManager = authenticationManager; this.securityContextRepository = securityContextRepository; this.csrfTokenRepository = csrfTokenRepository; this.users = users;
  }

  @GetMapping("/csrf")
  public Map<String, String> csrf(CsrfToken token) { return Map.of("token", token.getToken()); }

  @PostMapping("/login")
  public Map<String, Object> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
    final Authentication authentication;
    try {
      authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    } catch (DisabledException exception) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled. Contact your administrator.");
    } catch (LockedException exception) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is locked due to security policy.");
    } catch (AuthenticationException exception) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
    servletRequest.getSession(true);
    servletRequest.changeSessionId();
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    securityContextRepository.saveContext(context, servletRequest, response);
    csrfTokenRepository.saveToken(null, servletRequest, response);
    CsrfToken refreshedToken = csrfTokenRepository.generateToken(servletRequest);
    csrfTokenRepository.saveToken(refreshedToken, servletRequest, response);
    UserEntity user = users.findByUsername(authentication.getName()).orElseThrow();
    user.setLastLoginAt(LocalDateTime.now()); users.save(user);
    return userView(user);
  }

  @GetMapping("/me")
  public Map<String, Object> me(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to continue");
    return users.findByUsername(authentication.getName()).map(this::userView).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));
  }

  @PostMapping("/logout")
  public Map<String, String> logout(HttpServletRequest request, HttpServletResponse response) {
    SecurityContextHolder.clearContext();
    var session = request.getSession(false);
    if (session != null) session.invalidate();
    csrfTokenRepository.saveToken(null, request, response);
    return Map.of("message", "Signed out");
  }

  private Map<String, Object> userView(UserEntity user) {
    return Map.of("id", user.getId(), "username", user.getUsername(), "fullName", user.getFullName(), "role", user.getRole().getRoleCode(), "email", user.getEmail() == null ? "" : user.getEmail());
  }
}
