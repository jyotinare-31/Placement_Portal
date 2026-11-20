package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "candidate_skills", uniqueConstraints = @UniqueConstraint(name = "uq_candidate_skill", columnNames = {"candidate_id", "skill_id"}))
@Getter @Setter @NoArgsConstructor
public class CandidateSkillEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "candidate_skill_id") private Integer id;
  @Column(name = "candidate_id", nullable = false) private Integer candidateId;
  @Column(name = "skill_id", nullable = false) private Integer skillId;
  @Column(name = "proficiency_level", length = 20) private String proficiencyLevel;
  @Column(name = "years_experience", nullable = false, precision = 4, scale = 1) private BigDecimal yearsExperience = BigDecimal.ZERO;
  @Column(name = "is_primary", nullable = false) private Boolean primary = false;
}
