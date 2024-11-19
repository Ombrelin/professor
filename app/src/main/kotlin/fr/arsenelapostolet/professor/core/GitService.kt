package fr.arsenelapostolet.professor.core

import java.net.URI
import java.nio.file.Path

interface GitService {

    fun cloneRepository(localRepository: Path, repositoryURL: URI)
    fun repositoryExists(localRepository: Path): Boolean
    fun updateRepository(localRepository: Path)
}