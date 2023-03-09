package com.poli.internship.data.entity;

import jakarta.persistence.*;

@Entity(name = "application")
public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "position_id")
    private PositionEntity position;
    private Long curriculumId;


    protected ApplicationEntity(){};
    public ApplicationEntity(PositionEntity position, Long curriculumId) {
        this.position = position;
        this.curriculumId = curriculumId;
    }

    public Long getId() {
        return id;
    }

    public PositionEntity getPosition() {
        return position;
    }

    public Long getCurriculumId() {
        return curriculumId;
    }
}
