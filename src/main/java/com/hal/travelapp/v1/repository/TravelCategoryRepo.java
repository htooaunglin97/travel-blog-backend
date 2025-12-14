package com.hal.travelapp.v1.repository;

import com.hal.travelapp.v1.entity.domain.TravelCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TravelCategoryRepo extends JpaRepository<TravelCategory, Long> {
    List<TravelCategory> findByIdIn(Set<Long> ids);
}

