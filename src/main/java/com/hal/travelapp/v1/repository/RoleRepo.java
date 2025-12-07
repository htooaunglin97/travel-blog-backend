package com.hal.travelapp.v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hal.travelapp.v1.entity.domain.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

}
