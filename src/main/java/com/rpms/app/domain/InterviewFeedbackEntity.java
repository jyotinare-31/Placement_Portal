package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_feedback")
@Getter @Setter @NoArgsConstructor
public class InterviewFeedbackEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "feedback_id") private Integer id;
  @Column(name = "interview_id", nullable = false, unique = true) private Integer interviewId;
  @Column(name = "technical_rating", precision = 3, scale = 1) private BigDecimal technicalRating;
  @Column(name = "communication_rating", precision = 3, scale = 1) private BigDecimal communicationRating;
  @Column(name = "problem_solving_rating", precision = 3, scale = 1) private BigDecimal problemSolvingRating;
  @Column(name = "overall_rating", precision = 3, scale = 1) private BigDecimal overallRating;
  @Column(nullable = false, length = 30) private String recommendation;
  @Column(columnDefinition = "text") private String remarks;
  @Column(name = "submitted_by", nullable = false) private Integer submittedBy;
  @Column(name = "submitted_at", insertable = false, updatable = false) private LocalDateTime submittedAt;
}
