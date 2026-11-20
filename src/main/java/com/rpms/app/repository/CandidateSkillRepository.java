package com.rpms.app.repository;

import com.rpms.app.domain.CandidateSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkillEntity, Integer> {
  List<CandidateSkillEntity> findByCandidateId(Integer candidateId);
  boolean existsByCandidateIdAndSkillId(Integer candidateId, Integer skillId);
}
