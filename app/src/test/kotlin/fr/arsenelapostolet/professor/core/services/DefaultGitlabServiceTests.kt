package fr.arsenelapostolet.professor.core.services

import fr.arsenelapostolet.professor.core.entities.Grade
import fr.arsenelapostolet.professor.datafactories.StudentBuilder
import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DefaultGitlabServiceTests {


    val secretService = mockk<SecretService>()
    private val fakeStudentRepository = FakeStudentRepository()


    @Test
    fun `getMergeRequestsWithLabelComments, one project, one label and one merge request`() =
        runBlocking {
            // Given
            coEvery { secretService["GITLAB_TOKEN"] } returns System.getenv("GITLAB_TOKEN")!!
            val target = DefaultGitlabService(secretService)
            val student = StudentBuilder()
                .withProjectUrl("https://gitlab.com/hdurand/other-student-project")
                .withGrades(listOf<Grade>(Grade(BigDecimal(3), "livrable-1")))
                .withGitlabUsername("tdurand")
                .build()
            fakeStudentRepository.data[student.id] = student

            // When
            val result = target.getMergeRequestsWithLabelComments(
                listOf("Ombrelin/jgit-test-project"),
                listOf("test-label-1")
            )

            // Then
            assertEquals(1, result.size)
            assertEquals("test-label-1", result.single().label)
            assertEquals("Ombrelin/jgit-test-project", result.single().project)
            assertEquals(
                """
                    Notation livrable 1 :
                    
                    Fonctionnalités : 2/2
                    Qualité : 1.3/2

                    - 4 threads mineurs * 0.1 = 0.4pts
                    - 1 thread intermédiaire * 0.3 = 0.3pts

                    Total : 3.8/4
                """.trimIndent(),
                result.single().comment
            )
        }

    @Test
    fun `getMergeRequestsWithLabelComments, one project, two label and two merge request`() =
        runBlocking {
            // Given
            coEvery { secretService["GITLAB_TOKEN"] } returns System.getenv("GITLAB_TOKEN")!!
            val target = DefaultGitlabService(secretService)
            val student = StudentBuilder()
                .withProjectUrl("https://gitlab.com/hdurand/other-student-project")
                .withGrades(listOf<Grade>(Grade(BigDecimal(3), "livrable-1")))
                .withGitlabUsername("tdurand")
                .build()
            fakeStudentRepository.data[student.id] = student

            // When
            val result = target.getMergeRequestsWithLabelComments(
                listOf("Ombrelin/jgit-test-project"),
                listOf("test-label-1", "test-label-2")
            )

            // Then
            assertEquals(2, result.size)
            assertNotNull(result.single { it.label == "test-label-1" })
            assertNotNull(result.single { it.label == "test-label-2" })
            assertTrue(result.all { it.project == "Ombrelin/jgit-test-project" })
        }

    @Test
    fun `getMergeRequestsWithLabelComments, two projects, three labels and three merge reqest`() =
        runBlocking {
            // Given
            coEvery { secretService["GITLAB_TOKEN"] } returns System.getenv("GITLAB_TOKEN")!!
            val target = DefaultGitlabService(secretService)
            val student = StudentBuilder()
                .withProjectUrl("https://gitlab.com/hdurand/other-student-project")
                .withGrades(listOf<Grade>(Grade(BigDecimal(3), "livrable-1")))
                .withGitlabUsername("tdurand")
                .build()
            fakeStudentRepository.data[student.id] = student

            // When
            val result = target.getMergeRequestsWithLabelComments(
                listOf("Ombrelin/jgit-test-project"),
                listOf("test-label-1", "test-label-2", "test-label-3")
            )

            // Then
            assertEquals(3, result.size)
            assertNotNull(result.single { it.label == "test-label-1" })
            assertNotNull(result.single { it.label == "test-label-2" })
            assertNotNull(result.single { it.label == "test-label-3" })
            assertTrue(result.all { it.project == "Ombrelin/jgit-test-project" })
        }

}