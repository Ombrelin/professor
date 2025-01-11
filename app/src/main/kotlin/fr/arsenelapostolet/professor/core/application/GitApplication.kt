package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.core.repositories.StudentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

class GitApplication(private val studentRepository: StudentRepository, private val gitService: GitService) {

    suspend fun synchronizeGradesFromGitlab() {
        // TODO
    }

    suspend fun synchronizeLocalGitRepositories(localFolder: Path) {
        val duos = getDuos()

        for (duo in duos) {
            processDuo(localFolder, duo)
        }

        duos.map {
            coroutineScope {
                async {
                    processDuo(localFolder, it)
                }
            }
        }.awaitAll()

    }

    private fun getDuos(): Map<URI, List<Student>> = studentRepository
        .getAllStudents()
        .groupBy { it.projectUrl }

    private suspend fun processDuo(
        localFolder: Path,
        duo: Map.Entry<URI, List<Student>>,
    ) {
        val localRepositoryFolderPath = localFolder.resolve(getLocalRepositoryFolderName(duo.value))
        if (gitService.repositoryExists(localRepositoryFolderPath)) {
            gitService.updateRepository(localRepositoryFolderPath)
        } else {
            gitService.cloneRepository(localRepositoryFolderPath, duo.key)
        }
    }

    private fun getLocalRepositoryFolderName(duo: List<Student>): Path {
        val projectUrl = duo.first().projectUrl
        val usernames = duo.joinToString("-") { it.gitlabUsername }
        return Paths.get("${usernames}-${projectUrl.toString().split("/").last()}")
    }
}