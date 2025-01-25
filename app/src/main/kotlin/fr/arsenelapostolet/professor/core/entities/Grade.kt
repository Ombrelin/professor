package fr.arsenelapostolet.professor.core.entities

import java.math.BigDecimal

class Grade(var score: BigDecimal, val deliverable: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Grade

        if (score != other.score) return false
        if (deliverable != other.deliverable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = score.hashCode()
        result = 31 * result + deliverable.hashCode()
        return result
    }
}
