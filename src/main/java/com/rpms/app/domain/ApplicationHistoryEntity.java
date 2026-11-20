package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_history")
@Getter @Setter @NoArgsConstructor
public class ApplicationHistoryEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "history_id") private Long id;
  @Column(name = "application_id", nullable = false) private Integer applicationId;
  @Column(name = "old_status", length = 30) private String oldStatus;
  @Column(name = "new_status", nullable = false, length = 30) private String newStatus;
  @Column(length = 500) private String remarks;
  @Column(name = "changed_by", nullable = false) private Integer changedBy;
  @Column(name = "changed_at", insertable = false, updatable = false) private LocalDateTime changedAt;
}
