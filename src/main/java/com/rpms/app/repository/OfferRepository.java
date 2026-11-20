package com.rpms.app.repository;

import com.rpms.app.domain.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<OfferEntity, Integer> {
  boolean existsByApplicationId(Integer applicationId);
  long countByOfferStatus(String offerStatus);
}
