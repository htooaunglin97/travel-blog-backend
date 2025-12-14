package com.hal.travelapp.v1.repository;

import com.hal.travelapp.v1.entity.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepo extends JpaRepository<City, Long> {
    Optional<City> findById(Long id);
}

