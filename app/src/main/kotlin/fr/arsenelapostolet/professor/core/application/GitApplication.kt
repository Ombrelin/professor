package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.core.GitService
import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.core.repositories.StudentRepository
import java.nio.file.Path
import java.nio.file.Paths

class GitApplication(private val studentRepository: StudentRepository, private val gitService: GitService) {

    suspend fun syncLocalGitRepositories(localFolder: Path){
        val duos = studentRepository
            .getAllStudents()
            .groupBy { it.projectUrl }

        for (duo in duos) {
            val localRepositoryFolderPath = localFolder.resolve(getLocalRepositoryFolderName(duo.value))
            gitService.cloneRepository(localRepositoryFolderPath, duo.key)
        }
    }

    private fun getLocalRepositoryFolderName(duo: List<Student>): String {
        val projectUrl = duo.first().projectUrl
        val usernames = duo.map { it.gitlabUsername }.join("-")
        Paths.get("${student.gitlabUsername}-${student.projectUrl.toString().split("/").last()}")
    }
}