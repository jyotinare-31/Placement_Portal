package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter @Setter @NoArgsConstructor
public class CompanyEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "company_id") private Integer id;
  @Column(name = "company_code", nullable = false, unique = true, length = 30) private String companyCode;
  @Column(name = "company_name", nullable = false, length = 180) private String companyName;
  @Column(length = 100) private String industry;
  @Column(length = 200) private String website;
  @Column(length = 120) private String location;
  @Column(name = "company_type", length = 30) private String companyType;
  @Column(name = "hr_name", length = 120) private String hrName;
  @Column(name = "hr_email", length = 150) private String hrEmail;
  @Column(name = "hr_mobile", length = 20) private String hrMobile;
  @Column(nullable = false, length = 20) private String status = "ACTIVE";
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
