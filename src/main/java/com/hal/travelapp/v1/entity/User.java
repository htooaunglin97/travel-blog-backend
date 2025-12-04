package com.hal.travelapp.v1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_tbl")
@Getter
@Setter
public class User extends BaseEntity
{
}
