package com.hal.travelapp.v1.repository;

import com.hal.travelapp.v1.entity.domain.CertifiedUserRequest;
import com.hal.travelapp.v1.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertifiedUserRequestRepo extends JpaRepository<CertifiedUserRequest, Long> {
    
    Optional<CertifiedUserRequest> findByUserIdAndStatus(Long userId, RequestStatus status);
    
    Optional<CertifiedUserRequest> findByUserIdAndDeletedFalse(Long userId);
    
    List<CertifiedUserRequest> findByStatusAndDeletedFalse(RequestStatus status);
    
    List<CertifiedUserRequest> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);
}



