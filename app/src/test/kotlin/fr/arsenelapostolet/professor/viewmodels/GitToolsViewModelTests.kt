package fr.arsenelapostolet.professor.viewmodels

import fr.arsenelapostolet.professor.core.application.GitApplication
import fr.arsenelapostolet.professor.fakes.FakeGitService
import fr.arsenelapostolet.professor.fakes.FakeSecretService
import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import fr.arsenelapostolet.professor.viewmodels.utils.DialogService
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import java.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitToolsViewModelTests {

    private val mockClock = mockk<Clock>()
    private val fakeSecretService = FakeSecretService()
    private val mockDialogService = mockk<DialogService>()

    private val target = GitToolsViewModel(GitApplication(FakeStudentRepository(), FakeGitService(mockClock)), fakeSecretService, mockDialogService)

    @Test
    fun `init when Gitlab token doesn't exist in secrets and prompt result is null, then availability of token is false` () = runBlocking {
        // Given
        val tokenSecretName = "GITLAB_TOKEN"
        val token = "glpat-1234"
        val prompt = "Entrez votre token Gitlab"
        coEvery { mockDialogService.prompt(prompt) } returns null

        // When
        target.init()

        // Then
        assertNull(fakeSecretService[tokenSecretName])
        coVerify { mockDialogService.prompt(prompt) }
        assertFalse(target.gitlabTokenAvailable.value)
    }


    @Test
    fun `init when Gitlab token doesn't exist in secrets, prompt it via dialog`() = runBlocking {
        // Given
        val tokenSecretName = "GITLAB_TOKEN"
        val token = "glpat-1234"
        val prompt = "Entrez votre token Gitlab"
        coEvery { mockDialogService.prompt(prompt) } returns token

        // When
        target.init()

        // Then
        assertEquals(token, fakeSecretService[tokenSecretName])
        coVerify { mockDialogService.prompt(prompt) }
        assertTrue(target.gitlabTokenAvailable.value)
    }

    @Test
    fun `init when Gitlab token exists in secrets, don't prompt it via dialog`() = runBlocking {
        // Given
        val tokenSecretName = "GITLAB_TOKEN"
        val token = "glpat-1234"
        val prompt = "Entrez votre token Gitlab"
        coEvery { mockDialogService.prompt(prompt) } returns token
        fakeSecretService[tokenSecretName] = token

        // When
        target.init()

        // Then
        assertEquals(token, fakeSecretService[tokenSecretName])
        coVerify { mockDialogService wasNot Called }
        assertTrue(target.gitlabTokenAvailable.value)
    }

}