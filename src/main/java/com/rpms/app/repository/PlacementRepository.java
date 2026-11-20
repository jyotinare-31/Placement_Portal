package com.rpms.app.repository;

import com.rpms.app.domain.PlacementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlacementRepository extends JpaRepository<PlacementEntity, Integer> {
  boolean existsByOfferId(Integer offerId);
  long countByPlacementStatus(String placementStatus);
  long countByJoiningStatus(String joiningStatus);
}
