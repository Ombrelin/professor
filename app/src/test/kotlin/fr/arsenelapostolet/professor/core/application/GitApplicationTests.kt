package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.core.application.GitlabService.GradedMergeRequestComment
import fr.arsenelapostolet.professor.core.entities.Grade
import fr.arsenelapostolet.professor.datafactories.StudentBuilder
import fr.arsenelapostolet.professor.fakes.FakeGitService
import fr.arsenelapostolet.professor.fakes.FakeGitService.GitRepository
import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import fr.arsenelapostolet.professor.viewmodels.utils.ViewModelProperty
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.net.URI
import java.nio.file.Paths
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitApplicationTests {

    private val mockClock = mockk<Clock>()
    private val mockGitlabService = mockk<GitlabService>()
    private val fakeStudentRepository = FakeStudentRepository()
    private val fakeGitService = FakeGitService(mockClock)
    private val target = GitApplication(fakeStudentRepository, fakeGitService, mockGitlabService)


    @Test
    fun `synchronizeLocalGitRepositories when no existing local repository, then clone student repository`() =
        runBlocking {
            // Given
            val student = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("jdurand")
                .build()
            fakeStudentRepository.data[student.id] = student
            coEvery { mockClock.instant() } returns Instant.parse("2025-01-02T13:00:00Z")
            coEvery { mockClock.zone } returns ZoneId.systemDefault()
            val fakeLocalFolder = Paths.get("/home/arsene/git/test")

            // When
            target.synchronizeLocalGitRepositories(fakeLocalFolder, ViewModelProperty<Double>(0.0))

            // Then
            val repository = fakeGitService.repositories.values.single()
            assertEquals("/home/arsene/git/test/jdurand-student-project", repository.localRepositoryPath.toString())
            assertEquals(student.projectUrl, repository.url)
            assertTrue(repository.lastUpdate.isBefore(LocalDateTime.now()))
        }

    @Test
    fun `synchronizeLocalGitRepositories when duos, then create one repo by duo`() = runBlocking {
        // Given
        val student = StudentBuilder()
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("jdurand")
            .build()

        val otherStudent = StudentBuilder()
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("pdurand")
            .build()
        fakeStudentRepository.data[student.id] = student
        fakeStudentRepository.data[otherStudent.id] = otherStudent
        coEvery { mockClock.instant() } returns Instant.parse("2025-01-02T13:00:00Z")
        coEvery { mockClock.zone } returns ZoneId.systemDefault()
        val fakeLocalFolder = Paths.get("/home/arsene/git/test")

        // When
        target.synchronizeLocalGitRepositories(fakeLocalFolder, ViewModelProperty<Double>(0.0))

        // Then
        val repository = fakeGitService.repositories.values.single()
        assertEquals("/home/arsene/git/test/jdurand-pdurand-student-project", repository.localRepositoryPath.toString())
        assertEquals(student.projectUrl, repository.url)
        assertTrue(repository.lastUpdate.isBefore(LocalDateTime.now()))
    }

    @Test
    fun `synchronizeGradesFromGitlab, when two duos and multiple deliverables, and Merge Request found, one with existing grade, then update grade for corresponding student and create for the other`() =
        runBlocking {
            // Given
            val firstDuoStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("jdurand")
                .build()
            val firstDuoOtherStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("pdurand")
                .build()
            val secondDuoStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/hdurand/other-student-project")
                .withGitlabUsername("hdurand")
                .withGrades(listOf<Grade>(Grade(BigDecimal(3), "livrable-1")))
                .build()
            val secondDuoOtherStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/hdurand/other-student-project")
                .withGrades(listOf<Grade>(Grade(BigDecimal(3), "livrable-1")))
                .withGitlabUsername("tdurand")
                .build()
            fakeStudentRepository.data[firstDuoStudent.id] = firstDuoStudent
            fakeStudentRepository.data[firstDuoOtherStudent.id] = firstDuoOtherStudent
            fakeStudentRepository.data[secondDuoStudent.id] = secondDuoStudent
            fakeStudentRepository.data[secondDuoOtherStudent.id] = secondDuoOtherStudent

            val firstDuoComment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 3.3/4
            """.trimIndent()

            val secondDuoComment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 3.8/4
            """.trimIndent()

            coEvery {
                mockGitlabService.getMergeRequestsWithLabelComments(
                    listOf("jdurand/student-project", "hdurand/other-student-project"),
                    listOf(
                        "livrable-1-corrige",
                        "livrable-2-corrige",
                        "livrable-3-corrige",
                        "livrable-4-corrige",
                        "livrable-5-corrige"
                    )
                )
            } returns listOf(
                GradedMergeRequestComment("jdurand", firstDuoComment, "livrable-1-corrige"),
                GradedMergeRequestComment("hdurand", secondDuoComment, "livrable-1-corrige")
            )

            // When
            target.synchronizeGradesFromGitlab()

            // Then
            assertEquals(BigDecimal("3.3"), fakeStudentRepository.data[firstDuoStudent.id]!!.grades.single().score)
            assertEquals("livrable-1", fakeStudentRepository.data[firstDuoStudent.id]!!.grades.single().deliverable)

            assertEquals(BigDecimal("3.3"), fakeStudentRepository.data[firstDuoOtherStudent.id]!!.grades.single().score)
            assertEquals(
                "livrable-1",
                fakeStudentRepository.data[firstDuoOtherStudent.id]!!.grades.single().deliverable
            )

            assertEquals(BigDecimal("3.8"), fakeStudentRepository.data[secondDuoStudent.id]!!.grades.single().score)
            assertEquals("livrable-1", fakeStudentRepository.data[secondDuoStudent.id]!!.grades.single().deliverable)

            assertEquals(
                BigDecimal("3.8"),
                fakeStudentRepository.data[secondDuoOtherStudent.id]!!.grades.single().score
            )
            assertEquals(
                "livrable-1",
                fakeStudentRepository.data[secondDuoOtherStudent.id]!!.grades.single().deliverable
            )

        }

    @Test
    fun `synchronizeLocalGitRepositories when repository exists, then update it`() = runBlocking {
        // Given
        val student = StudentBuilder()
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("jdurand")
            .build()

        val otherStudent = StudentBuilder()
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("pdurand")
            .build()
        fakeStudentRepository.data[student.id] = student
        fakeStudentRepository.data[otherStudent.id] = otherStudent

        val fakeLocalFolder = Paths.get("/home/arsene/git/test")


        val localRepositoryPath = Paths.get("/home/arsene/git/test/jdurand-pdurand-student-project")
        fakeGitService.repositories[localRepositoryPath] = GitRepository(
            localRepositoryPath,
            URI("https://gitlab.com/jdurand/student-project"),
            LocalDateTime.MIN
        )

        coEvery { mockClock.instant() } returns LocalDateTime
            .of(2025, 1, 2, 14, 0, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
        coEvery { mockClock.zone } returns ZoneId.systemDefault()

        // When
        target.synchronizeLocalGitRepositories(fakeLocalFolder, ViewModelProperty<Double>(0.0))

        // Then
        assertEquals(
            LocalDateTime.of(2025, 1, 2, 14, 0, 0),
            fakeGitService.repositories[localRepositoryPath]!!.lastUpdate
        )
    }

    @Test
    fun `synchronizeGradesFromGitlab, when single student and Merge Request found and no existing grade, then create grade for corresponding student`() =
        runBlocking {
            // Given
            val student = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("jdurand")
                .build()
            fakeStudentRepository.data[student.id] = student

            val comment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 3.3/4
            """.trimIndent()

            coEvery {
                mockGitlabService.getMergeRequestsWithLabelComments(
                    listOf("jdurand/student-project"),
                    listOf(
                        "livrable-1-corrige",
                        "livrable-2-corrige",
                        "livrable-3-corrige",
                        "livrable-4-corrige",
                        "livrable-5-corrige"
                    )
                )
            } returns listOf(
                GradedMergeRequestComment(
                    "jdurand",
                    comment,
                    "livrable-1-corrige"
                )
            )

            // When
            target.synchronizeGradesFromGitlab()

            // Then
            assertEquals(BigDecimal("3.3"), fakeStudentRepository.data[student.id]!!.grades.single().score)
            assertEquals("livrable-1", fakeStudentRepository.data[student.id]!!.grades.single().deliverable)
        }


    @Test
    fun `synchronizeGradesFromGitlab, when single student and Merge Request found and one existing grade for this deliverable, then update grade for corresponding student`() =
        runBlocking {
            // Given
            val student = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("jdurand")
                .withGrades(listOf<Grade>(Grade(BigDecimal(3), "livrable-1")))
                .build()
            fakeStudentRepository.data[student.id] = student

            val comment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 3.3/4
            """.trimIndent()

            coEvery {
                mockGitlabService.getMergeRequestsWithLabelComments(
                    listOf("jdurand/student-project"),
                    listOf(
                        "livrable-1-corrige",
                        "livrable-2-corrige",
                        "livrable-3-corrige",
                        "livrable-4-corrige",
                        "livrable-5-corrige"
                    )
                )
            } returns listOf(GradedMergeRequestComment("jdurand", comment, "livrable-1-corrige"))

            // When
            target.synchronizeGradesFromGitlab()

            // Then
            assertEquals(BigDecimal("3.3"), fakeStudentRepository.data[student.id]!!.grades.single().score)
            assertEquals("livrable-1", fakeStudentRepository.data[student.id]!!.grades.single().deliverable)
        }

    @Test
    fun `synchronizeGradesFromGitlab, when one duo and Merge Request found and no existing grade, then create grade for corresponding student`() =
        runBlocking {
            // Given
            val student = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("jdurand")
                .build()
            val otherStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("pdurand")
                .build()
            fakeStudentRepository.data[student.id] = student
            fakeStudentRepository.data[otherStudent.id] = otherStudent

            val comment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 3.3/4
            """.trimIndent()

            coEvery {
                mockGitlabService.getMergeRequestsWithLabelComments(
                    listOf("jdurand/student-project"),
                    listOf(
                        "livrable-1-corrige",
                        "livrable-2-corrige",
                        "livrable-3-corrige",
                        "livrable-4-corrige",
                        "livrable-5-corrige"
                    )
                )
            } returns listOf(GradedMergeRequestComment("jdurand", comment, "livrable-1-corrige"))

            // When
            target.synchronizeGradesFromGitlab()

            // Then
            assertEquals(BigDecimal("3.3"), fakeStudentRepository.data[student.id]!!.grades.single().score)
            assertEquals("livrable-1", fakeStudentRepository.data[student.id]!!.grades.single().deliverable)
            assertEquals(BigDecimal("3.3"), fakeStudentRepository.data[otherStudent.id]!!.grades.single().score)
            assertEquals("livrable-1", fakeStudentRepository.data[otherStudent.id]!!.grades.single().deliverable)
        }


    @Test
    fun `synchronizeGradesFromGitlab, when two duos, and Merge Request found, one with existing grade, then update grade for corresponding student and create for the other`() =
        runBlocking {
            // Given
            val firstDuoStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("jdurand")
                .build()
            val firstDuoOtherStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/jdurand/student-project")
                .withGitlabUsername("pdurand")
                .build()
            val secondDuoStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/hdurand/other-student-project")
                .withGitlabUsername("hdurand")
                .withGrades(listOf<Grade>(Grade(BigDecimal(3), "livrable-1")))
                .build()
            val secondDuoOtherStudent = StudentBuilder()
                .withProjectUrl("https://gitlab.com/hdurand/other-student-project")
                .withGrades(listOf<Grade>(Grade(BigDecimal(3), "livrable-1")))
                .withGitlabUsername("tdurand")
                .build()
            fakeStudentRepository.data[firstDuoStudent.id] = firstDuoStudent
            fakeStudentRepository.data[firstDuoOtherStudent.id] = firstDuoOtherStudent
            fakeStudentRepository.data[secondDuoStudent.id] = secondDuoStudent
            fakeStudentRepository.data[secondDuoOtherStudent.id] = secondDuoOtherStudent

            val firstDuoDeliverable1Comment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 3.3/4
            """.trimIndent()

            val secondDeliverable1Comment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 3.8/4
            """.trimIndent()

            val firstDuoDeliverable2Comment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 1.2/4
            """.trimIndent()

            val secondDeliverable2Comment = """
                Notation livrable 1 :

                Fonctionnalités : 2/2
                Qualité : 1.3/2

                4 threads mineurs * 0.1 = 0.4pts
                1 thread intermédiaire * 0.3 = 0.3pts

                Total : 2/4
            """.trimIndent()

            coEvery {
                mockGitlabService.getMergeRequestsWithLabelComments(
                    listOf("jdurand/student-project", "hdurand/other-student-project"),
                    listOf(
                        "livrable-1-corrige",
                        "livrable-2-corrige",
                        "livrable-3-corrige",
                        "livrable-4-corrige",
                        "livrable-5-corrige"
                    )
                )
            } returns listOf(
                GradedMergeRequestComment("jdurand", firstDuoDeliverable1Comment, "livrable-1-corrige"),
                GradedMergeRequestComment(
                    "hdurand",
                    secondDeliverable1Comment,
                    "livrable-1-corrige"
                ),
                GradedMergeRequestComment("jdurand", firstDuoDeliverable2Comment, "livrable-2-corrige"),
                GradedMergeRequestComment(
                    "hdurand",
                    secondDeliverable2Comment,
                    "livrable-2-corrige"
                )
            )

            // When
            target.synchronizeGradesFromGitlab()

            // Then
            assertEquals(
                BigDecimal("3.3"),
                fakeStudentRepository.data[firstDuoStudent.id]!!.grades.single { it.deliverable == "livrable-1" }.score
            )
            assertEquals(
                BigDecimal("1.2"),
                fakeStudentRepository.data[firstDuoStudent.id]!!.grades.single { it.deliverable == "livrable-2" }.score
            )

            assertEquals(
                BigDecimal("3.3"),
                fakeStudentRepository.data[firstDuoOtherStudent.id]!!.grades.single { it.deliverable == "livrable-1" }.score
            )
            assertEquals(
                BigDecimal("1.2"),
                fakeStudentRepository.data[firstDuoOtherStudent.id]!!.grades.single { it.deliverable == "livrable-2" }.score
            )

            assertEquals(
                BigDecimal("3.8"),
                fakeStudentRepository.data[secondDuoStudent.id]!!.grades.single { it.deliverable == "livrable-1" }.score
            )
            assertEquals(
                BigDecimal("2"),
                fakeStudentRepository.data[secondDuoStudent.id]!!.grades.single { it.deliverable == "livrable-2" }.score
            )

            assertEquals(
                BigDecimal("3.8"),
                fakeStudentRepository.data[secondDuoOtherStudent.id]!!.grades.single { it.deliverable == "livrable-1" }.score
            )
            assertEquals(
                BigDecimal("2"),
                fakeStudentRepository.data[secondDuoOtherStudent.id]!!.grades.single { it.deliverable == "livrable-2" }.score
            )


        }


}