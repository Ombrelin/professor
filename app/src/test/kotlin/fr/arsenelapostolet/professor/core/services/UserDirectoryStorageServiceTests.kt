package fr.arsenelapostolet.professor.core.services

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertTrue

class UserDirectoryStorageServiceTests {

    val target = UserDirectoryStorageService()

    @Test
    fun `getStorageRepository returns valid path of existing directory`() {
        // When
        val result = target.getStorageDirectoryPath()

        // Then
        assertTrue(Files.exists(result))
    }

}