package com.poli.internship.data.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;

@Entity(name = "application")
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"position_id", "user_id"})
})
public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "position_id")
    private PositionEntity position;

    protected ApplicationEntity(){};
    public ApplicationEntity(PositionEntity position, Long userId) {
        this.position = position;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public PositionEntity getPosition() {
        return position;
    }

    public Long getUserId() {
        return userId;
    }
}
