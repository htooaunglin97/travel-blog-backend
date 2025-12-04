package com.hal.travelapp.v1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate()
    {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate()
    {
        updatedAt = Instant.now();
    }


    @Override
    public final int hashCode()
    {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
