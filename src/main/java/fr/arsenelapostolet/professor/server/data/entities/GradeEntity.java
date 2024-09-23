package fr.arsenelapostolet.professor.server.data.entities;

import fr.arsenelapostolet.professor.server.core.entities.Grade;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "grade")
public class GradeEntity {

    @Id
    @GeneratedValue()
    private UUID id;

    private BigDecimal score;

    @ManyToOne(fetch = FetchType.LAZY)
    private DeliverableEntity deliverable;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudentEntity student;

    public GradeEntity(Grade grade, StudentEntity student) {
        this.score = grade.getScore();
        this.deliverable = new DeliverableEntity(grade.getDeliverable());
        this.student = student;
    }

    public GradeEntity() {

    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public DeliverableEntity getDeliverable() {
        return deliverable;
    }

    public void setDeliverable(DeliverableEntity deliverable) {
        this.deliverable = deliverable;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
