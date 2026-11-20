package com.rpms.app.repository;

import com.rpms.app.domain.ApplicationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationHistoryRepository extends JpaRepository<ApplicationHistoryEntity, Long> {
  List<ApplicationHistoryEntity> findByApplicationIdOrderByChangedAtDesc(Integer applicationId);
}
