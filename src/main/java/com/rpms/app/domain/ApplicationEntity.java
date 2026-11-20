package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(name = "uq_candidate_job", columnNames = {"candidate_id", "job_id"}))
@Getter @Setter @NoArgsConstructor
public class ApplicationEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "application_id") private Integer id;
  @Column(name = "application_code", nullable = false, unique = true, length = 40) private String applicationCode;
  @Column(name = "candidate_id", nullable = false) private Integer candidateId;
  @Column(name = "job_id", nullable = false) private Integer jobId;
  @Column(name = "applied_at", insertable = false, updatable = false) private LocalDateTime appliedAt;
  @Column(name = "eligibility_status", nullable = false, length = 20) private String eligibilityStatus = "PENDING";
  @Column(name = "eligibility_reason", length = 500) private String eligibilityReason;
  @Column(name = "application_status", nullable = false, length = 30) private String applicationStatus = "APPLIED";
  @Column(length = 50) private String source;
  @Column(name = "assigned_recruiter_id") private Integer assignedRecruiterId;
  @Column(name = "updated_at", insertable = false, updatable = false) private LocalDateTime updatedAt;
}
