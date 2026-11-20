package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
@Getter @Setter @NoArgsConstructor
public class OfferEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "offer_id") private Integer id;
  @Column(name = "offer_code", nullable = false, unique = true, length = 40) private String offerCode;
  @Column(name = "application_id", nullable = false, unique = true) private Integer applicationId;
  @Column(nullable = false, length = 120) private String designation;
  @Column(name = "offered_package_lpa", nullable = false, precision = 6, scale = 2) private BigDecimal offeredPackageLpa;
  @Column(name = "offer_date", nullable = false) private LocalDate offerDate;
  @Column(name = "expected_joining_date") private LocalDate expectedJoiningDate;
  @Column(name = "offer_status", nullable = false, length = 20) private String offerStatus = "OFFERED";
  @Column(length = 500) private String remarks;
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
