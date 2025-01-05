package fr.arsenelapostolet.professor.core.services

import de.swiesend.secretservice.simple.SimpleCollection
import kotlin.test.Test
import kotlin.test.assertEquals

class FreeDesktopSecretServiceTests {

    private val secretName = "GITLAB_TOKEN"
    private val secretValue = "glpat-123456"

    val target = FreeDesktopSecretService()

    @Test
    fun `get, when existing secret, then returns it`() {
        // Given

        SimpleCollection().createItem(secretName, secretValue)

        // When
        val result = target[secretName]

        // Then
        assertEquals(secretValue, result)
    }

    @Test
    fun `set, create secret`() {
        // Given
        val otherSecretValue = "987654321"

        // When
        target[secretName] = otherSecretValue

        // Then
        assertEquals(otherSecretValue, target[secretName])
    }

}