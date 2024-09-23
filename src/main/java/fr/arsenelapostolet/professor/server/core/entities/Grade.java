package fr.arsenelapostolet.professor.server.core.entities;

import java.math.BigDecimal;

public class Grade {

    private final BigDecimal score;
    private final Deliverable deliverable;

    public Grade(BigDecimal score, Deliverable deliverable) {
        this.score = score;
        this.deliverable = deliverable;
    }

    public BigDecimal getScore() {
        return score;
    }

    public Deliverable getDeliverable() {
        return deliverable;
    }
}
