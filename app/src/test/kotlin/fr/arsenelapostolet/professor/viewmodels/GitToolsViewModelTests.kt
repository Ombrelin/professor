package fr.arsenelapostolet.professor.viewmodels

import fr.arsenelapostolet.professor.core.application.GitApplication
import fr.arsenelapostolet.professor.core.application.GitlabService
import fr.arsenelapostolet.professor.fakes.FakeGitService
import fr.arsenelapostolet.professor.fakes.FakeSecretService
import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import fr.arsenelapostolet.professor.viewmodels.utils.DialogService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import java.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitToolsViewModelTests {

    private val mockClock = mockk<Clock>()
    private val mockGitlabService = mockk<GitlabService>()
    private val fakeSecretService = FakeSecretService()
    private val mockDialogService = mockk<DialogService>()

    private val target = GitToolsViewModel(
        GitApplication(
            FakeStudentRepository(),
            FakeGitService(mockClock),
            mockGitlabService
        ),
        fakeSecretService,
        mockDialogService
    )

    @Test
    fun `init when Gitlab token doesn't exist, token is unavailable`() = runBlocking {
        // Given
        val tokenSecretName = "GITLAB_TOKEN"

        // When
        target.init()

        // Then
        assertNull(fakeSecretService[tokenSecretName])
        assertFalse(target.gitlabTokenAvailable.value)
    }

    @Test
    fun `init when Gitlab token exists in secrets, don't prompt it via dialog and it's available`() = runBlocking {
        // Given
        val tokenSecretName = "GITLAB_TOKEN"
        val token = "glpat-1234"
        fakeSecretService[tokenSecretName] = token

        // When
        target.init()

        // Then
        assertEquals(token, fakeSecretService[tokenSecretName])
        assertTrue(target.gitlabTokenAvailable.value)
    }

    @Test
    fun `updateGitlabToken when Gitlab token doesn't exists in secret, prompt it, store it in secrets and token is available`() =
        runBlocking {
            // Given
            val tokenSecretName = "GITLAB_TOKEN"
            val token = "glpat-1234"

            coEvery { mockDialogService.prompt("Entrez votre token Gitlab") } returns token

            // When
            target.updateGitlabToken()

            // Then
            assertEquals(token, fakeSecretService[tokenSecretName])
            assertTrue(target.gitlabTokenAvailable.value)
        }

    @Test
    fun `updateGitlabToken when Gitlab token exists in secret, prompt it, store it in secrets and token is available`() =
        runBlocking {
            // Given
            val tokenSecretName = "GITLAB_TOKEN"
            val token = "glpat-1234"
            val newToken = "glpat-456"

            fakeSecretService[tokenSecretName] = token

            coEvery { mockDialogService.prompt("Entrez votre token Gitlab") } returns newToken

            // When
            target.updateGitlabToken()

            // Then
            assertEquals(newToken, fakeSecretService[tokenSecretName])
            assertTrue(target.gitlabTokenAvailable.value)
        }

}