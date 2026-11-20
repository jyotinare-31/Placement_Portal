package com.rpms.app.repository;

import com.rpms.app.domain.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<CandidateEntity, Integer> {
  boolean existsByEmail(String email);
  boolean existsByMobile(String mobile);
}
