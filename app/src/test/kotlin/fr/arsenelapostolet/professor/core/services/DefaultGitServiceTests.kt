package fr.arsenelapostolet.professor.core.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultGitServiceTests {

    val secretService = mockk<SecretService>()


    @Test
    fun `secret fetched at call time not construct time`(@TempDir temporaryDirectory: Path) =
        runBlocking {
            // Given
            coEvery { secretService["GITLAB_TOKEN"] } returns System.getenv("GITLAB_TOKEN")!!

            // When
            DefaultGitService(secretService)

            // Then
            coVerify(exactly = 0) { secretService["GITLAB_TOKEN"] }
        }

    @Test
    fun `cloneRepository with non existing repository clones repository to local folder`(@TempDir temporaryDirectory: Path) =
        runBlocking {
            // Given
            coEvery { secretService["GITLAB_TOKEN"] } returns System.getenv("GITLAB_TOKEN")!!
            val target = DefaultGitService(secretService)
            val localRepositoryDirectory = Paths.get(temporaryDirectory.toString(), "test-repo")

            // When

            target.cloneRepository(localRepositoryDirectory, URI("https://gitlab.com/Ombrelin/efrei-adv-java-project"))

            // Then
            Files.exists(localRepositoryDirectory)
            val files = Files.list(localRepositoryDirectory).toList()
            assertContains(files, Paths.get(localRepositoryDirectory.toString(), "ruleset.xml"))
        }

    @Test
    fun `repositoryExists with existing repository, returns true`(@TempDir temporaryDirectory: Path) = runBlocking {
        // Given
        coEvery { secretService["GITLAB_TOKEN"] } returns System.getenv("GITLAB_TOKEN")!!
        val target = DefaultGitService(secretService)
        val localRepositoryDirectory = Paths.get(temporaryDirectory.toString(), "test-repo")
        Git.init().setDirectory(File(localRepositoryDirectory.toString())).call()

        // When
        val result = target.repositoryExists(localRepositoryDirectory)

        // Then
        assertTrue(result)
    }

    @Test
    fun `repositoryExists with non existing repository and not existing folder, returns false`(@TempDir temporaryDirectory: Path) =
        runBlocking {
            // Given
            coEvery { secretService["GITLAB_TOKEN"] } returns System.getenv("GITLAB_TOKEN")!!
            val target = DefaultGitService(secretService)
            val localRepositoryDirectory = Paths.get(temporaryDirectory.toString(), "test-repo")

            // When
            val result = target.repositoryExists(localRepositoryDirectory)

            // Then
            assertFalse(result)
        }

    @Test
    fun `repositoryExists with non existing repository, returns false`(@TempDir temporaryDirectory: Path) =
        runBlocking {
            // Given
            coEvery { secretService["GITLAB_TOKEN"] } returns System.getenv("GITLAB_TOKEN")!!
            val target = DefaultGitService(secretService)
            val localRepositoryDirectory = Paths.get(temporaryDirectory.toString(), "test-repo")
            Files.createDirectory(localRepositoryDirectory)

            // When
            val result = target.repositoryExists(localRepositoryDirectory)

            // Then
            assertFalse(result)
        }

    @Test
    fun `updateRepository with existing repository, pull new changes from rempte`(@TempDir temporaryDirectory: Path) =
        runBlocking {
            // Given
            val gitlabToken = System.getenv("GITLAB_TOKEN")!!
            coEvery { secretService["GITLAB_TOKEN"] } returns gitlabToken
            val target = DefaultGitService(secretService)
            val localRepositoryDirectoryUpdate = Paths.get(temporaryDirectory.toString(), "test-repo-update")
            val localRepositoryDirectory = Paths.get(temporaryDirectory.toString(), "test-repo")
            target.cloneRepository(localRepositoryDirectoryUpdate, URI("https://gitlab.com/Ombrelin/jgit-test-project"))
            target.cloneRepository(localRepositoryDirectory, URI("https://gitlab.com/Ombrelin/jgit-test-project"))

            val updateRepo = Git.open(localRepositoryDirectoryUpdate.toFile())

            val updateReadme = File(localRepositoryDirectoryUpdate.toString(), "README.md")
            val content = updateReadme.readText()
            val expectedContent = content + content
            updateReadme.writeText(expectedContent)

            updateRepo.add().addFilepattern(".").call()
            updateRepo.commit().setMessage("Test running").call()
            updateRepo.push().setCredentialsProvider(UsernamePasswordCredentialsProvider("Ombrelin", gitlabToken))
                .call()

            // When
            target.updateRepository(localRepositoryDirectory)

            // Then
            val readme = File(localRepositoryDirectory.toString(), "README.md")
            val readMeContent = readme.readText()

            assertEquals(readMeContent, expectedContent)
        }

}