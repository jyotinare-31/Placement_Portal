package com.rpms.app.config;

import com.rpms.app.domain.RoleEntity;
import com.rpms.app.domain.UserEntity;
import com.rpms.app.repository.RoleRepository;
import com.rpms.app.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BootstrapAdminInitializer implements ApplicationRunner {
  private final Environment environment;
  private final UserRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder passwordEncoder;

  public BootstrapAdminInitializer(Environment environment, UserRepository users, RoleRepository roles, PasswordEncoder passwordEncoder) {
    this.environment = environment; this.users = users; this.roles = roles; this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    String adminUsername = environment.getProperty("RPMS_BOOTSTRAP_ADMIN_USERNAME", "admin");
    String adminPassword = environment.getProperty("RPMS_BOOTSTRAP_ADMIN_PASSWORD", "Admin@123456");
    String resetAdminPassword = environment.getProperty("RPMS_RESET_ADMIN_PASSWORD");

    UserEntity adminUser = users.findByUsername(adminUsername.trim()).orElseGet(UserEntity::new);
    if (!isBcryptHash(adminUser.getPasswordHash())) {
      RoleEntity adminRole = roles.findByRoleCode("ADMIN").orElseThrow(() -> new IllegalStateException("The ADMIN role must exist before bootstrapping the first administrator."));
      adminUser.setUsername(adminUsername.trim());
      if (adminUser.getFullName() == null || adminUser.getFullName().isBlank()) adminUser.setFullName("System Admin");
      if (adminUser.getEmail() == null || adminUser.getEmail().isBlank()) adminUser.setEmail("admin@example.com");
      adminUser.setRole(adminRole);
      adminUser.setStatus("ACTIVE");
      adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
      users.save(adminUser);
    } else if (resetAdminPassword != null && !resetAdminPassword.isBlank()) {
      adminUser.setPasswordHash(passwordEncoder.encode(resetAdminPassword.trim()));
      adminUser.setStatus("ACTIVE");
      users.save(adminUser);
    }

    for (UserEntity user : users.findAll()) {
      if (!isBcryptHash(user.getPasswordHash())) {
        String defaultPassword = switch (user.getRole().getRoleCode()) {
          case "ADMIN" -> "Admin@123456";
          case "RECRUITER" -> "Recruiter@123";
          case "PLACEMENT_OFFICER" -> "Placement@123";
          default -> "Password@1234";
        };
        user.setPasswordHash(passwordEncoder.encode(defaultPassword));
        users.save(user);
      }
    }
  }

  private boolean isBcryptHash(String value) {
    return value != null && value.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
  }
}
