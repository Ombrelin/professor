package fr.arsenelapostolet.professor.repositories

import app.cash.sqldelight.db.SqlDriver
import fr.arsenelapostolet.data.professor.StudentQueries
import fr.arsenelapostolet.professor.Database
import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.core.repositories.StudentRepository
import java.net.URI

class SqlDelightStudentRepository constructor(val driver: SqlDriver) : StudentRepository {

    override fun saveStudents(students: Set<Student>): Set<Student> {
        val database = Database(driver)
        val studentQueries: StudentQueries = database.studentQueries

        for (student in students) {
            studentQueries.insert(
                id = student.id,
                first_name = student.firstName,
                last_name = student.lastName,
                email = student.email,
                gitlab_username = student.gitlabUsername,
                project_url = student.projectUrl.toString(),
                efrei_class = student.efreiClass
            )
        }
        return students
    }

    override fun getAllStudents(): Set<Student> {
        val database = Database(driver)
        val studentQueries: StudentQueries = database.studentQueries

        return studentQueries.selectAll().executeAsList().map {
            Student(
                it.id,
                it.first_name,
                it.last_name,
                it.email,
                it.gitlab_username,
                emptySet(),
                it.efrei_class,
                URI.create(it.project_url)
            )
        }.toSet()
    }
}
