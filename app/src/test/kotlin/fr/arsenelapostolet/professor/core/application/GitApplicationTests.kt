package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.datafactories.StudentBuilder
import fr.arsenelapostolet.professor.fakes.FakeGitService
import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitApplicationTests {

    private val fakeStudentRepository = FakeStudentRepository()
    private val fakeGitService = FakeGitService()
    private val target = GitApplication(fakeStudentRepository, fakeGitService)

    @Test
    fun `syncLocalGitRepositories when no existing local repository, then clone student repository`() = runBlocking {
        // Given
        val student = StudentBuilder
            .withProjectUrl("https://gitlab.com/jdurand/student-project")
            .withGitlabUsername("jdurand")
            .build()
        fakeStudentRepository.data[student.id] = student

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

        val fakeLocalFolder = Paths.get("/home/arsene/git/test")

        // When
        target.syncLocalGitRepositories(fakeLocalFolder)

        // Then
        val repository = fakeGitService.repositories.values.single()
        assertEquals("/home/arsene/git/test/jdurand-pdurand-student-project", repository.localRepositoryPath.toString())
        assertEquals(student.projectUrl, repository.url)
        assertTrue(repository.lastUpdate.isBefore(LocalDateTime.now()))
    }


}