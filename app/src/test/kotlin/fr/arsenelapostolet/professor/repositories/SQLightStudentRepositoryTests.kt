package fr.arsenelapostolet.professor.repositories

import fr.arsenelapostolet.professor.core.entities.Grade
import fr.arsenelapostolet.professor.core.repositories.StudentRepository
import fr.arsenelapostolet.professor.datafactories.StudentBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.math.BigDecimal
import java.nio.file.Path
import java.sql.DriverManager
import kotlin.test.Test

class SQLightStudentRepositoryTests {

    private lateinit var studentRepository: StudentRepository

    @BeforeEach
    fun setUpDatabase(@TempDir temporaryDirectory: Path) {
        val url = "jdbc:sqlite:${temporaryDirectory.resolve("student.db").toAbsolutePath()}"
        println(url)
        val connection = DriverManager
            .getConnection(url);
        studentRepository = SQLightStudentRepository(connection)
    }

    @Test
    fun `save, when non existing student, appears in getAllStudents` (){
        // Given
        val student = StudentBuilder()
            .build()

        // When
        studentRepository.saveStudents(setOf(student))

        // Then
        val students = studentRepository.getAllStudents()
        assertEquals(1, students.size)
        val studentFromDb = students.single()
        assertEquals(student, studentFromDb)
    }

    @Test
    fun `save, when non existing student with grade, appears in getAllStudents` (){
        // Given
        val student = StudentBuilder()
            .withGrades(listOf(Grade(BigDecimal("3.5"), "livrable-1")))
            .build()

        // When
        studentRepository.saveStudents(setOf(student))

        // Then
        val students = studentRepository.getAllStudents()
        assertEquals(1, students.size)
        val studentFromDb = students.single()
        assertEquals(student, studentFromDb)
    }

    @Test
    fun `save, when existing student, update their grades` (){
        // Given
        val student = StudentBuilder()
            .build()
        studentRepository.saveStudents(setOf(student))
        student.grades.add(Grade(BigDecimal("3.5"), "livrable-1"))

        // When
        studentRepository.saveStudents(setOf(student))

        // Then
        val students = studentRepository.getAllStudents()
        assertEquals(1, students.size)
        val studentFromDb = students.single()
        assertEquals(student, studentFromDb)
    }


    @Test
    fun `save, when non existing students, appear in getAllStudents` (){
        // Given
        val student = StudentBuilder()
            .build()

        val otherStudent = StudentBuilder()
            .withGitlabUsername("tdurand")
            .withEmail("tdurand@test.fr")
            .build()

        // When
        studentRepository.saveStudents(setOf(student, otherStudent))

        // Then
        val students = studentRepository.getAllStudents()
        assertEquals(2, students.size)
        val studentFromDb = students.first()
        assertEquals(student, studentFromDb)
        val otherStudentFromDb = students.last()
        assertEquals(otherStudent, otherStudentFromDb)
    }
}