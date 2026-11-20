package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "interview_rounds", uniqueConstraints = @UniqueConstraint(name = "uq_application_round", columnNames = {"application_id", "round_number"}))
@Getter @Setter @NoArgsConstructor
public class InterviewEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "interview_id") private Integer id;
  @Column(name = "application_id", nullable = false) private Integer applicationId;
  @Column(name = "round_number", nullable = false) private Integer roundNumber;
  @Column(name = "round_type", nullable = false, length = 50) private String roundType;
  @Column(name = "scheduled_date", nullable = false) private LocalDate scheduledDate;
  @Column(name = "scheduled_time") private LocalTime scheduledTime;
  @Column(nullable = false, length = 20) private String mode;
  @Column(name = "interviewer_name", length = 120) private String interviewerName;
  @Column(name = "meeting_or_location", length = 255) private String meetingOrLocation;
  @Column(nullable = false, length = 20) private String status = "SCHEDULED";
  @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
}
