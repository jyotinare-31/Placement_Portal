package com.rpms.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locations", uniqueConstraints = @UniqueConstraint(name = "uq_location", columnNames = {"city_name", "state_name", "country_name"}))
@Getter @Setter @NoArgsConstructor
public class LocationEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "location_id") private Integer id;
  @Column(name = "city_name", nullable = false, length = 100) private String cityName;
  @Column(name = "state_name", nullable = false, length = 100) private String stateName;
  @Column(name = "country_name", nullable = false, length = 100) private String countryName = "India";
  @Column(name = "is_active", nullable = false) private Boolean active = true;
}
