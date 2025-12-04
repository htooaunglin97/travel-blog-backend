package com.hal.travelapp.v1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "travel_category_tbl")
@Getter
@Setter
public class TravelCategory extends BaseEntity
{
    private String name;
}
