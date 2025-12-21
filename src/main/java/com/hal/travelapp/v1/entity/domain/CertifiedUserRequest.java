package com.hal.travelapp.v1.entity.domain;

import com.hal.travelapp.v1.entity.BaseEntity;
import com.hal.travelapp.v1.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "certified_user_request_tbl")
@Getter
@Setter
public class CertifiedUserRequest extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    private String rejectionReason;


}

