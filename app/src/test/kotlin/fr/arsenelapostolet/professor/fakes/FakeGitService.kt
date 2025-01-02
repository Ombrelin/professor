package fr.arsenelapostolet.professor.fakes

import fr.arsenelapostolet.professor.core.application.GitService
import java.net.URI
import java.nio.file.Path
import java.time.Clock
import java.time.LocalDateTime

class FakeGitService : GitService {

    val repositories: MutableMap<Path, GitRepository>
    private val clock: Clock

    constructor(clock: Clock) {
        this.repositories = mutableMapOf<Path, GitRepository>()
        this.clock = clock
    }


    override suspend fun cloneRepository(localRepository: Path, repositoryURL: URI) {
        repositories[localRepository] = GitRepository(localRepository, repositoryURL, LocalDateTime.now())
    }

    override suspend fun repositoryExists(localRepository: Path): Boolean {
        return repositories.containsKey(localRepository)
    }

    override suspend fun updateRepository(localRepository: Path) {
        repositories[localRepository]?.lastUpdate = LocalDateTime.now(clock)
    }

    data class GitRepository(
        val localRepositoryPath: Path,
        val url: URI,
        var lastUpdate: LocalDateTime,
    )
}