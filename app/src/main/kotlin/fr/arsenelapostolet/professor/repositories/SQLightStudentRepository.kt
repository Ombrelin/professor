package fr.arsenelapostolet.professor.repositories

import fr.arsenelapostolet.professor.core.entities.Grade
import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.core.repositories.StudentRepository
import java.net.URI
import java.sql.Connection
import java.sql.ResultSet

class SQLightStudentRepository : StudentRepository {


    companion object {
        val studentSchema = """
            CREATE TABLE IF NOT EXISTS student (
                    id TEXT PRIMARY KEY NOT NULL ON CONFLICT IGNORE,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE ,
                    gitlab_username TEXT NOT NULL UNIQUE ON CONFLICT IGNORE,
                    efrei_class TEXT NOT NULL,
                    project_url TEXT NOT NULL
            );
        """.trimIndent()

        val gradeSchema = """
            CREATE TABLE IF NOT EXISTS grade (
                studentId TEXT NOT NULL,
                score REAL NOT NULL,
                deliverable TEXT NOT NULL,
                PRIMARY KEY (studentId, deliverable),
                FOREIGN KEY(studentId) REFERENCES student(id)
                ON DELETE CASCADE
            );
        """.trimIndent()

        val studentInsertQuery = """
            INSERT INTO student(id, first_name, last_name, email, gitlab_username, efrei_class, project_url)
            VALUES (?, ?, ?, ?, ?, ?, ?);
        """.trimIndent()

        val getAllStudentsQuery = """
            SELECT *
            FROM student;
        """.trimIndent()

        val upsertGradeQuery = """
            INSERT INTO grade(studentId, score, deliverable)
            VALUES (?, ?, ?)
            ON CONFLICT(studentId, deliverable) DO
                UPDATE
                SET studentId = excluded.studentId;
        """.trimIndent()

        val getGradesForStudentQuery = """
            SELECT *
            FROM grade
            WHERE studentId = ?;
        """.trimIndent()
    }

    val connection: Connection

    constructor(connection: Connection) {
        this.connection = connection
        connection
            .createStatement()
            .execute(studentSchema)
        connection
            .createStatement()
            .execute(gradeSchema)
    }


    private fun insertStudent(student: Student) {
        connection
            .prepareStatement(studentInsertQuery)
            .use {
                it.setString(1, student.id)
                it.setString(2, student.firstName)
                it.setString(3, student.lastName)
                it.setString(4, student.email)
                it.setString(5, student.gitlabUsername)
                it.setString(6, student.efreiClass)
                it.setString(7, student.projectUrl.toString())
                it.execute()
            }
        for (grade in student.grades) {
            connection
                .prepareStatement(upsertGradeQuery)
                .use {
                    it.setString(1, student.id)
                    it.setBigDecimal(2, grade.score)
                    it.setString(3, grade.deliverable)
                    it.execute()
                }
        }
    }

    override fun saveStudents(students: Set<Student>): Set<Student> {
        for (student in students) {
            insertStudent(student)
        }
        return students
    }

    override fun getAllStudents(): Set<Student> {
        val resultSet = connection
            .createStatement()
            .executeQuery(getAllStudentsQuery)

        return generateSequence {
            if (resultSet.next()) mapStudent(resultSet) else null
        }.toSet()
    };

    private fun mapStudent(resultSet: ResultSet): Student {
        return Student(
            resultSet.getString("id"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name"),
            resultSet.getString("email"),
            resultSet.getString("gitlab_username"),
            getGradesForStudent(resultSet.getString("id")),
            resultSet.getString("efrei_class"),
            URI.create(resultSet.getString("project_url"))
        )
    }

    private fun getGradesForStudent(studentId: String): MutableList<Grade> {
        val resultSet = connection
            .prepareStatement(getGradesForStudentQuery)
            .let {
                it.setString(1, studentId)
                it.executeQuery()
            }

        return generateSequence {
            if (resultSet.next()) mapGrade(resultSet) else null
        }.toMutableList()
    }

    private fun mapGrade(resultSet: ResultSet): Grade =
        Grade(resultSet.getBigDecimal("score"), resultSet.getString("deliverable"))
}
