package com.hal.travelapp.v1.entity.domain;

import com.hal.travelapp.v1.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "best_time_to_visit_tbl")
@Getter
@Setter
public class BestTimeToVisit extends BaseEntity
{
    @OneToOne
    @JoinColumn(name = "blog_id")
    private TravelBlog travelBlog;

    private int startMonth;

    private int endMonth;
}
