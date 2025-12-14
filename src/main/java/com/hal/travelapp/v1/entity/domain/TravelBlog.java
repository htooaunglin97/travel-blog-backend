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

    @Column(length = 2000)
    private String mainPhotoUrl;

    @Column(length = 1000)
    private String paragraph1;

    @Column(length = 1000)
    private String paragraph2;

    @Column(length = 1000)
    private String paragraph3;

    @Column(length = 2000)
    private String midPhoto1Url;

    @Column(length = 2000)
    private String midPhoto2Url;

    @Column(length = 2000)
    private String midPhoto3Url;

    @Column(length = 2000)
    private String sidePhotoUrl;

    @ManyToOne
    private City city;

    @ManyToOne
    private User author;

    @Enumerated(EnumType.STRING)
    private BlogStatus status = BlogStatus.PENDING;

    @OneToOne(mappedBy = "travelBlog", cascade = CascadeType.ALL)
    private BestTimeToVisit bestTimeToVisit;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "blog_category_tbl",
            joinColumns        = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<TravelCategory> travelCategory;

    public enum BlogStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

}
