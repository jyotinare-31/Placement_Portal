package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "placements")
@Getter @Setter @NoArgsConstructor
public class PlacementEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "placement_id") private Integer id;
  @Column(name = "candidate_id", nullable = false) private Integer candidateId;
  @Column(name = "company_id", nullable = false) private Integer companyId;
  @Column(name = "job_id", nullable = false) private Integer jobId;
  @Column(name = "offer_id", nullable = false, unique = true) private Integer offerId;
  @Column(nullable = false, length = 120) private String designation;
  @Column(name = "final_package_lpa", nullable = false, precision = 6, scale = 2) private BigDecimal finalPackageLpa;
  @Column(name = "expected_joining_date") private LocalDate expectedJoiningDate;
  @Column(name = "actual_joining_date") private LocalDate actualJoiningDate;
  @Column(name = "joining_status", nullable = false, length = 20) private String joiningStatus = "PENDING";
  @Column(name = "placement_status", nullable = false, length = 20) private String placementStatus = "ACTIVE";
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
