package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor
public class JobEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "job_id") private Integer id;
  @Column(name = "job_code", nullable = false, unique = true, length = 40) private String jobCode;
  @Column(name = "company_id", nullable = false) private Integer companyId;
  @Column(name = "job_title", nullable = false, length = 150) private String jobTitle;
  @Column(name = "employment_type", nullable = false, length = 30) private String employmentType = "FULL_TIME";
  @Column(nullable = false, length = 120) private String location;
  @Column(nullable = false) private Integer openings;
  @Column(name = "min_package_lpa", precision = 6, scale = 2) private BigDecimal minPackageLpa;
  @Column(name = "max_package_lpa", precision = 6, scale = 2) private BigDecimal maxPackageLpa;
  @Column(name = "min_percentage", precision = 5, scale = 2) private BigDecimal minPercentage;
  @Column(name = "min_passing_year") private Short minPassingYear;
  @Column(name = "max_passing_year") private Short maxPassingYear;
  @Column(name = "max_backlogs", nullable = false) private Integer maxBacklogs = 0;
  @Column(name = "experience_min_years", nullable = false, precision = 4, scale = 1) private BigDecimal experienceMinYears = BigDecimal.ZERO;
  @Column(name = "qualification_rule", length = 255) private String qualificationRule;
  @Column(name = "posted_date", nullable = false) private LocalDate postedDate;
  @Column(name = "application_deadline", nullable = false) private LocalDate applicationDeadline;
  @Column(nullable = false, length = 20) private String status = "OPEN";
  @Column(name = "created_by", nullable = false) private Integer createdBy;
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
