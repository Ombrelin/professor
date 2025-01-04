package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.datafactories.StudentBuilder
import fr.arsenelapostolet.professor.fakes.FakeGitService
import fr.arsenelapostolet.professor.fakes.FakeGitService.GitRepository
import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
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
    private val fakeStudentRepository = FakeStudentRepository()
    private val fakeGitService = FakeGitService(mockClock)
    private val target = GitApplication(fakeStudentRepository, fakeGitService)


    @Test
    fun `syncLocalGitRepositories when no existing local repository, then clone student repository`() = runBlocking {
        // Given
        val student = StudentBuilder
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("jdurand")
            .build()
        fakeStudentRepository.data[student.id] = student
        coEvery { mockClock.instant() } returns Instant.parse("2025-01-02T13:00:00Z")
        coEvery { mockClock.zone } returns ZoneId.systemDefault()
        val fakeLocalFolder = Paths.get("/home/arsene/git/test")

        // When
        target.syncLocalGitRepositories(fakeLocalFolder)

        // Then
        val repository = fakeGitService.repositories.values.single()
        assertEquals("/home/arsene/git/test/jdurand-student-project", repository.localRepositoryPath.toString())
        assertEquals(student.projectUrl, repository.url)
        assertTrue(repository.lastUpdate.isBefore(LocalDateTime.now()))
    }

    @Test
    fun `syncLocalGitRepositories when duos, then create one repo by duo`() = runBlocking {
        // Given
        val student = StudentBuilder
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("jdurand")
            .build()

        val otherStudent = StudentBuilder
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("pdurand")
            .build()
        fakeStudentRepository.data[student.id] = student
        fakeStudentRepository.data[otherStudent.id] = otherStudent
        coEvery { mockClock.instant() } returns Instant.parse("2025-01-02T13:00:00Z")
        coEvery { mockClock.zone } returns ZoneId.systemDefault()
        val fakeLocalFolder = Paths.get("/home/arsene/git/test")

        // When
        target.syncLocalGitRepositories(fakeLocalFolder)

        // Then
        val repository = fakeGitService.repositories.values.single()
        assertEquals("/home/arsene/git/test/jdurand-pdurand-student-project", repository.localRepositoryPath.toString())
        assertEquals(student.projectUrl, repository.url)
        assertTrue(repository.lastUpdate.isBefore(LocalDateTime.now()))
    }

    @Test
    fun `syncLocalGitRepositories when repository exists, then update it`() = runBlocking {
        // Given
        val student = StudentBuilder
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("jdurand")
            .build()

        val otherStudent = StudentBuilder
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

        coEvery { mockClock.instant() } returns Instant.parse("2025-01-02T13:00:00Z")
        coEvery { mockClock.zone } returns ZoneId.systemDefault()

        // When
        target.syncLocalGitRepositories(fakeLocalFolder)

        // Then
        assertEquals(
            fakeGitService.repositories[localRepositoryPath]!!.lastUpdate,
            LocalDateTime.of(2025, 1, 2, 14, 0, 0)
        )
    }

}