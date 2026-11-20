package com.rpms.app.repository;

import com.rpms.app.domain.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Integer> {
  boolean existsByCandidateIdAndJobId(Integer candidateId, Integer jobId);
  long countByApplicationStatus(String applicationStatus);
}
