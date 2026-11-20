package com.rpms.app.repository;

import com.rpms.app.domain.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<SkillEntity, Integer> {
  boolean existsBySkillNameIgnoreCase(String skillName);
}
