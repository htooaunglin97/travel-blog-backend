package com.hal.travelapp.v1.entity.domain;

import com.hal.travelapp.v1.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "state_and_division_tbl")
@Getter
@Setter
public class City extends BaseEntity
{
    private String name;
}
