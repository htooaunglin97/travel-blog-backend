package com.hal.travelapp.v1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "travel_blog_tbl")
@Getter
@Setter
public class TravelBlog extends BaseEntity
{

    private String title;

    private String description;

    private String imageUrl;

    @ManyToOne
    private StateAndDivision stateAndDivision;

    @OneToOne(mappedBy = "travelBlog", cascade = CascadeType.ALL)
    private BestTimeToVisit bestTimeToVisit;

}
