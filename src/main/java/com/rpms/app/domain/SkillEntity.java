package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skills")
@Getter @Setter @NoArgsConstructor
public class SkillEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "skill_id") private Integer id;
  @Column(name = "skill_name", nullable = false, unique = true, length = 100) private String skillName;
  @Column(length = 60) private String category;
  @Column(name = "is_active", nullable = false) private Boolean active = true;
}
