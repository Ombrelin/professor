package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GradeApplicationTests {

    private val studentRepository = FakeStudentRepository()
    private val target = GradesApplication(studentRepository)

    @Test
    fun `when creating a new class, the csv is correctly parsed`() {
        // Given
        val csvLines = GradeApplicationTests::class.java.getResource("/students.csv")!!
            .readText()
            .lines()

        // When
        val result = target.createClass("lsi1", csvLines)

        // Then
        assertEquals("lsi1", result.name)
        assertEquals(result.name, "lsi1")
        assertEquals(result.students.size, csvLines.size - 1)
        for (student in result.students) {
            assertNotNull(student.id)
            assertEquals(0, student.grades.size)
            assertNotNull(student.firstName)
            assertNotNull(student.lastName)
            assertNotNull(student.email)
            assertContains(student.email, "@")
            assertNotNull(student.gitlabUsername)
            assertNotNull(student.projectUrl)
        }
    }

}