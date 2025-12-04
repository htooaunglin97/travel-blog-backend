package com.hal.travelapp.v1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "state_and_division_tbl")
@Getter
@Setter
public class StateAndDivision extends BaseEntity
{
    private String name;
}
