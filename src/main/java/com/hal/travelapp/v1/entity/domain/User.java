package com.hal.travelapp.v1.entity.domain;

import com.hal.travelapp.v1.entity.BaseEntity;
import jakarta.persistence.Column;
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
    String name;

    @Column(unique = true)
    String email;

    String password;
}
