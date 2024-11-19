package fr.arsenelapostolet.professor.fakes

import fr.arsenelapostolet.professor.core.GitService
import java.net.URI
import java.nio.file.Path
import java.time.LocalDateTime

class FakeGitService : GitService {

    val repositories = mutableMapOf<Path, GitRepository>()

    override fun cloneRepository(localRepository: Path, repositoryURL: URI) {
        repositories[localRepository] = GitRepository(localRepository, repositoryURL, LocalDateTime.now())
    }

    override fun repositoryExists(localRepository: Path): Boolean {
        return repositories.containsKey(localRepository)
    }

    override fun updateRepository(localRepository: Path) {
        repositories[localRepository]?.lastUpdate = LocalDateTime.now()
    }

    data class GitRepository(
        val localRepositoryPath: Path,
        val url: URI,
        var lastUpdate: LocalDateTime
    )
}