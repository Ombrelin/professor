package fr.arsenelapostolet.professor.core.services

import fr.arsenelapostolet.professor.core.application.GitService
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

class DefaultGitService(secretService: SecretService) : GitService {

    val credentialsProvider = UsernamePasswordCredentialsProvider("Ombrelin", secretService["GITLAB_TOKEN"])

    override suspend fun cloneRepository(localRepository: Path, repositoryURL: URI) {
        Git.cloneRepository()
            .setCredentialsProvider(credentialsProvider)
            .setURI(repositoryURL.toString())
            .setDirectory(File(localRepository.toString()))
            .call();
    }

    override suspend fun repositoryExists(localRepository: Path): Boolean {
        if (localRepository.exists()) {
            return localRepository.listDirectoryEntries(".git").size == 1
        }
        return false;
    }

    override suspend fun updateRepository(localRepository: Path) {
        val repository = Git.open(localRepository.toFile())
        repository.fetch().setCredentialsProvider(credentialsProvider).call()
        repository.pull().setCredentialsProvider(credentialsProvider).call()
    }
}