package com.rpms.app.repository;

import com.rpms.app.domain.InterviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface InterviewRepository extends JpaRepository<InterviewEntity, Integer> {
  boolean existsByApplicationIdAndRoundNumber(Integer applicationId, Integer roundNumber);
  List<InterviewEntity> findByScheduledDateGreaterThanEqualAndStatusOrderByScheduledDateAsc(LocalDate date, String status);
  long countByStatus(String status);
}
