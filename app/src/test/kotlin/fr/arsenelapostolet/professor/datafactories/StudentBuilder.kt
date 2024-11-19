package fr.arsenelapostolet.professor.datafactories

import fr.arsenelapostolet.professor.core.entities.Grade
import fr.arsenelapostolet.professor.core.entities.Student
import java.net.URI

object StudentBuilder {
    var id = 1

    var firstname = "Jean"
    var lastname = "Dupont"
    var email = "jean@dupont.fr"
    var gitlabUsername = "jdpt"
    var grades = emptySet<Grade>()
    var efreiClass = "lsi"
    var gitlabProjectUrl = URI.create("http://gitlab.com/student-project")

    fun build(): Student = Student((id++).toString(), firstname, lastname, email, gitlabUsername, grades, efreiClass, gitlabProjectUrl)
    fun withProjectUrl(projectURI: String): StudentBuilder {
        this.gitlabProjectUrl = URI(projectURI)
        return this
    }

    fun withGitlabUsername(gitlabUsername: String): StudentBuilder {
        this.gitlabUsername = gitlabUsername
        return this
    }
}