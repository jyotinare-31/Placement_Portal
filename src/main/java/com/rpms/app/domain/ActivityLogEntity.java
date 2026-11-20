package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Getter @Setter @NoArgsConstructor
public class ActivityLogEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "log_id") private Long id;
  @Column(name = "user_id") private Integer userId;
  @Column(name = "module_name", nullable = false, length = 60) private String moduleName;
  @Column(name = "action_name", nullable = false, length = 80) private String actionName;
  @Column(name = "record_id", length = 60) private String recordId;
  @Column(length = 500) private String description;
  @Column(name = "logged_at", insertable = false, updatable = false) private LocalDateTime loggedAt;
}
