package fr.arsenelapostolet.professor.core.services

import fr.arsenelapostolet.professor.core.application.StorageService
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class UserDirectoryStorageService : StorageService {
    override fun getStorageDirectoryPath(): Path {
        val path = Paths.get(System.getProperty("user.home") + "/.professor")

        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }

        return path;
    }
}