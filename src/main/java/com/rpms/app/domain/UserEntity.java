package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class UserEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "user_id") private Integer id;
  @Column(nullable = false, unique = true, length = 80) private String username;
  @Column(name = "password_hash", nullable = false, length = 255) private String passwordHash;
  @Column(name = "full_name", nullable = false, length = 120) private String fullName;
  @Column(length = 150, unique = true) private String email;
  @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "role_id", nullable = false) private RoleEntity role;
  @Column(nullable = false, length = 20) private String status = "ACTIVE";
  @Column(name = "last_login_at") private LocalDateTime lastLoginAt;
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
