package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "job_skills", uniqueConstraints = @UniqueConstraint(name = "uq_job_skill", columnNames = {"job_id", "skill_id"}))
@Getter @Setter @NoArgsConstructor
public class JobSkillEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "job_skill_id") private Integer id;
  @Column(name = "job_id", nullable = false) private Integer jobId;
  @Column(name = "skill_id", nullable = false) private Integer skillId;
  @Column(name = "minimum_level", length = 20) private String minimumLevel;
  @Column(name = "mandatory_flag", nullable = false) private Boolean mandatory = true;
}
