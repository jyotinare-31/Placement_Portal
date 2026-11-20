package com.rpms.app.repository;

import com.rpms.app.domain.InterviewFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedbackEntity, Integer> {
  boolean existsByInterviewId(Integer interviewId);
}
