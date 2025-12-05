package com.hal.travelapp.v1.entity.domain;

import com.hal.travelapp.v1.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "blog_category_tbl",
            joinColumns        = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<TravelCategory> travelCategory;

}
