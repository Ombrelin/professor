package fr.arsenelapostolet.professor.datafactories

import fr.arsenelapostolet.professor.core.entities.Grade
import fr.arsenelapostolet.professor.core.entities.Student
import java.net.URI

class StudentBuilder {
    companion object {
        var id = 1
    }

    private var firstname = "Jean"
    private var lastname = "Dupont"
    private var email = "jean@dupont.fr"
    private var gitlabUsername = "jdpt"
    private var grades = mutableListOf<Grade>()
    private var efreiClass = "lsi"
    private var gitlabProjectUrl = URI.create("http://gitlab.com/student-project")

    fun build(): Student =
        Student((id++).toString(), firstname, lastname, email, gitlabUsername, grades, efreiClass, gitlabProjectUrl)

    fun withProjectUrl(projectURI: String): StudentBuilder {
        this.gitlabProjectUrl = URI(projectURI)
        return this
    }

    fun withGitlabUsername(gitlabUsername: String): StudentBuilder {
        this.gitlabUsername = gitlabUsername
        return this
    }

    fun withGrades(grades: List<Grade>): StudentBuilder {
        this.grades.addAll(grades)
        return this;
    }
}