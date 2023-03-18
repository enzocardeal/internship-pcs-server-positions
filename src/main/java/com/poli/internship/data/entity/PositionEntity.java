package com.poli.internship.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.time.LocalDate;

@Entity(name = "position")
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String positionName;
    private String company;
    private String role;
    private LocalDate startsAt;
    private LocalDate endsAt;

    protected PositionEntity(){}
    public PositionEntity(Long userId, String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt) {
        this.userId = userId;
        this.positionName = positionName;
        this.company = company;
        this.role = role;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public PositionEntity(Long id, Long userId, String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt) {
        this.id = id;
        this.userId = userId;
        this.positionName = positionName;
        this.company = company;
        this.role = role;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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
