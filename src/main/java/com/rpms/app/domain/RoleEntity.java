package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor
public class RoleEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "role_id") private Integer id;
  @Column(name = "role_code", nullable = false, unique = true, length = 30) private String roleCode;
  @Column(name = "role_name", nullable = false, length = 80) private String roleName;
  @Column(name = "is_active", nullable = false) private Boolean active = true;
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
