package fr.arsenelapostolet.professor.core.application

import java.net.URI
import java.nio.file.Path

interface GitService {

    suspend fun cloneRepository(localRepository: Path, repositoryURL: URI)

    suspend fun repositoryExists(localRepository: Path): Boolean
    suspend fun updateRepository(localRepository: Path)
}