package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
        val result = target.importStudents(csvLines)

        // Then
        assertEquals(result.size, csvLines.size - 1)

        val firstStudent = result.first()
        assertEquals("lsi1", firstStudent.efreiClass)
        assertEquals("40284284", firstStudent.id)
        assertEquals("Tessy", firstStudent.firstName)
        assertEquals("Grundey", firstStudent.lastName)
        assertEquals("tgrundey0@free.fr", firstStudent.email)
        assertEquals("Tessy Grundey", firstStudent.fullName)
        assertEquals("mlitda12", firstStudent.gitlabUsername)
        assertTrue(firstStudent.projectUrl.toString().contains("http"))

        for (student in result) {
            assertNotNull(student.id)
            assertEquals(0, student.grades.size)
            assertNotNull(student.firstName)
            assertNotNull(student.lastName)
            assertNotNull(student.email)
            assertContains(student.email, "@")
            assertNotNull(student.gitlabUsername)
            assertNotNull(student.projectUrl)
            assertContains(listOf("lsi1", "lsi2"), student.efreiClass)
        }
    }

}