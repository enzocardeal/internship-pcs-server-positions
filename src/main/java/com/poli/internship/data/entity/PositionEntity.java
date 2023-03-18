package com.poli.internship.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity(name = "position")
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String positionName;
    private String company;
    private String role;
    private LocalDate startsAt;
    private LocalDate endsAt;

    protected PositionEntity(){}
    public PositionEntity(String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt) {
        this.positionName = positionName;
        this.company = company;
        this.role = role;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public PositionEntity(Long id, String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt) {
        this.id = id;
        this.positionName = positionName;
        this.company = company;
        this.role = role;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public Long getId() {
        return id;
    }

    public String getPositionName() {
        return positionName;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getStartsAt() {
        return startsAt;
    }

    public LocalDate getEndsAt() {
        return endsAt;
    }

}
