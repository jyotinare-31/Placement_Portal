package com.rpms.app.repository;

import com.rpms.app.domain.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<JobEntity, Integer> {
  List<JobEntity> findByStatusOrderByCreatedAtDesc(String status);
  long countByStatus(String status);
}
