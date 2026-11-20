package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates")
@Getter @Setter @NoArgsConstructor
public class CandidateEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "candidate_id") private Integer id;
  @Column(name = "candidate_code", nullable = false, unique = true, length = 30) private String candidateCode;
  @Column(name = "full_name", nullable = false, length = 150) private String fullName;
  @Column(nullable = false, unique = true, length = 150) private String email;
  @Column(nullable = false, unique = true, length = 20) private String mobile;
  @Column(name = "date_of_birth") private LocalDate dateOfBirth;
  @Column(length = 20) private String gender;
  @Column(nullable = false, length = 80) private String qualification;
  @Column(length = 180) private String college;
  @Column(length = 180) private String university;
  @Column(name = "passing_year", nullable = false) private Short passingYear;
  @Column(precision = 5, scale = 2) private BigDecimal percentage;
  @Column(precision = 4, scale = 2) private BigDecimal cgpa;
  @Column(name = "backlog_count", nullable = false) private Integer backlogCount = 0;
  @Column(name = "primary_technology", length = 80) private String primaryTechnology;
  @Column(name = "current_location", length = 100) private String currentLocation;
  @Column(name = "preferred_location", length = 100) private String preferredLocation;
  @Column(name = "profile_status", nullable = false, length = 20) private String profileStatus = "ACTIVE";
  @Column(name = "placement_status", nullable = false, length = 20) private String placementStatus = "NOT_PLACED";
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
  @Column(name = "updated_at", insertable = false, updatable = false) private LocalDateTime updatedAt;
}
