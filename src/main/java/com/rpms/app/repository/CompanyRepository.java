package com.rpms.app.repository;

import com.rpms.app.domain.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer> {
  Optional<CompanyEntity> findByCompanyNameIgnoreCase(String companyName);
}
