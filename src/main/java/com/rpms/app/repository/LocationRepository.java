package com.rpms.app.repository;

import com.rpms.app.domain.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {}
