package fr.arsenelapostolet.professor.server.data.entities;

import fr.arsenelapostolet.professor.server.core.entities.Deliverable;
import jakarta.persistence.*;

import java.util.Set;

@Entity(name = "Deliverable")
public class DeliverableEntity {

    @Id
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "deliverable", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<GradeEntity> grades;

    public DeliverableEntity(Deliverable deliverable) {
        this.name = deliverable.getName();
    }

    public DeliverableEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<GradeEntity> getGrades() {
        return grades;
    }

    public void setGrades(Set<GradeEntity> grades) {
        this.grades = grades;
    }
}
