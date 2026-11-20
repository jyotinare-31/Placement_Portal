package com.rpms.app.repository;

import com.rpms.app.domain.JobSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobSkillRepository extends JpaRepository<JobSkillEntity, Integer> {
  List<JobSkillEntity> findByJobId(Integer jobId);
  boolean existsByJobIdAndSkillId(Integer jobId, Integer skillId);
}
