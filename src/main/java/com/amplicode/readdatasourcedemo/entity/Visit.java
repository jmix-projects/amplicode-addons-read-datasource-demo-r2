package com.amplicode.readdatasourcedemo.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit")
public class Visit extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "visit_start", nullable = false)
    private LocalDateTime visitStart;

    @Column(name = "visit_end", nullable = false)
    private LocalDateTime visitEnd;

    @Column(name = "description", length = 4000)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getVisitEnd() {
        return visitEnd;
    }

    public void setVisitEnd(LocalDateTime visitEnd) {
        this.visitEnd = visitEnd;
    }

    public LocalDateTime getVisitStart() {
        return visitStart;
    }

    public void setVisitStart(LocalDateTime visitStart) {
        this.visitStart = visitStart;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}