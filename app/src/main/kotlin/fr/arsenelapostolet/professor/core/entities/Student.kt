package fr.arsenelapostolet.professor.core.entities

import java.net.URI

class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val gitlabUsername: String,
    val grades: MutableList<Grade>,
    val efreiClass: String,
    val projectUrl: URI,
) {
    val fullName: String
        get() = "$firstName $lastName"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Student

        if (id != other.id) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (email != other.email) return false
        if (gitlabUsername != other.gitlabUsername) return false
        if (grades.size != other.grades.size) return false

        for (gradePair in grades.zip(other.grades)) {
            if (!gradePair.first.equals(gradePair.second)) {
                return false
            }
        }

        if (efreiClass != other.efreiClass) return false
        if (projectUrl != other.projectUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + gitlabUsername.hashCode()
        result = 31 * result + grades.hashCode()
        result = 31 * result + efreiClass.hashCode()
        result = 31 * result + projectUrl.hashCode()
        return result
    }


}