package com.poli.internship.data.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

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
    @ElementCollection
    @CollectionTable(name="steps")
    private List<String> steps;
    private String benefits;
    private String area;
    private String location;
    private String scholarship;

    protected PositionEntity(){}

    public PositionEntity(
            Long userId,
            String positionName,
            String company,
            String role,
            LocalDate startsAt,
            LocalDate endsAt,
            List<String> steps,
            String benefits,
            String area,
            String location,
            String scholarship
    ) {
        this.userId = userId;
        this.positionName = positionName;
        this.company = company;
        this.role = role;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.steps = steps;
        this.benefits = benefits;
        this.area = area;
        this.location = location;
        this.scholarship = scholarship;
    }

    public PositionEntity(
            Long id,
            Long userId,
            String positionName,
            String company,
            String role,
            LocalDate startsAt,
            LocalDate endsAt,
            List<String> steps,
            String benefits,
            String area,
            String location,
            String scholarship
    ) {
        this.id = id;
        this.userId = userId;
        this.positionName = positionName;
        this.company = company;
        this.role = role;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.steps = steps;
        this.benefits = benefits;
        this.area = area;
        this.location = location;
        this.scholarship = scholarship;
    }

//    public PositionEntity(Long userId, String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt) {
//        this.userId = userId;
//        this.positionName = positionName;
//        this.company = company;
//        this.role = role;
//        this.startsAt = startsAt;
//        this.endsAt = endsAt;
//    }
//
//    public PositionEntity(Long id, Long userId, String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt) {
//        this.id = id;
//        this.userId = userId;
//        this.positionName = positionName;
//        this.company = company;
//        this.role = role;
//        this.startsAt = startsAt;
//        this.endsAt = endsAt;
//    }

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

    public List<String> getSteps() {
        return steps;
    }

    public String getBenefits() {
        return benefits;
    }

    public String getArea() {
        return area;
    }

    public String getLocation() {
        return location;
    }

    public String getScholarship() {
        return scholarship;
    }
}
