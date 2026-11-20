package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "qualifications")
@Getter @Setter @NoArgsConstructor
public class QualificationEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "qualification_id") private Integer id;
  @Column(name = "qualification_name", nullable = false, unique = true, length = 100) private String qualificationName;
  @Column(name = "is_active", nullable = false) private Boolean active = true;
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
