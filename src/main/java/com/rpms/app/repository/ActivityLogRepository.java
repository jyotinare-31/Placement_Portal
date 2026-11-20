package com.rpms.app.repository;

import com.rpms.app.domain.ActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {
  List<ActivityLogEntity> findTop100ByOrderByLoggedAtDesc();
}
