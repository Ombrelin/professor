package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.core.entities.Grade
import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.core.repositories.StudentRepository
import fr.arsenelapostolet.professor.core.services.DefaultGitService
import fr.arsenelapostolet.professor.viewmodels.utils.ViewModelProperty
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

class GitApplication(
    private val studentRepository: StudentRepository,
    private val gitService: GitService,
    private val gitlabService: GitlabService,
) {

    companion object {
        private val deliverables = listOf(
            "livrable-1",
            "livrable-2",
            "livrable-3",
            "livrable-4",
            "livrable-5"
        )

        val logger: Logger = LoggerFactory.getLogger(GitApplication::class.java)
    }

    suspend fun synchronizeGradesFromGitlab() {
        val duos = getDuos()
        val projectsNames = duos
            .keys
            .map { extractProjectNameFromGitlabUrl(it) }

        val comments =
            gitlabService.getMergeRequestsWithLabelComments(projectsNames, deliverables.map { "${it}-corrige" })

        for (comment in comments) {
            updateStudentsGrades(comment, duos)
        }
        studentRepository.saveStudents(duos.flatMap { it.value }.toSet())
    }

    private fun updateStudentsGrades(
        comment: GitlabService.GradedMergeRequestComment,
        duos: Map<URI, List<Student>>,
    ) {
        val grade = parseGrade(comment)
        val duo = duos
            .values
            .single { it.any { student -> student.projectUrl.toString().contains(comment.project) } }

        val deliverable = comment.label.replace("-corrige", "")

        for (student in duo) {

            val existingGradeForThisDeliverable = student.grades.firstOrNull { it.deliverable == deliverable }

            if (existingGradeForThisDeliverable == null) {
                student.grades.add(Grade(grade, deliverable))
            } else {
                existingGradeForThisDeliverable.score = grade
            }

        }
    }

    private fun parseGrade(comment: GitlabService.GradedMergeRequestComment): BigDecimal {
        val grade = comment.comment.split("\n")
            .filter { it.contains("Total") }
            .map { it.split(" ").last().split("/").first() }
            .map { BigDecimal(it) }
            .single()
        return grade
    }

    suspend fun synchronizeLocalGitRepositories(localFolder: Path, progressPercent: ViewModelProperty<Double>) {
        val duos = getDuos()
        var progressCount = 0
        progressPercent.value = 0.0
        duos.map {
            coroutineScope {
                async {
                    processDuo(localFolder, it)
                    progressCount++
                    progressPercent.value = progressCount.toDouble() / duos.size.toDouble()
                }
            }
        }.awaitAll()

    }

    private fun getDuos(): Map<URI, List<Student>> = studentRepository
        .getAllStudents()
        .groupBy { it.projectUrl }

    private suspend fun processDuo(
        localFolder: Path,
        duo: Map.Entry<URI, List<Student>>
    ) {
        try {
            val localRepositoryFolderPath = localFolder.resolve(getLocalRepositoryFolderName(duo.value))
            if (gitService.repositoryExists(localRepositoryFolderPath)) {
                gitService.updateRepository(localRepositoryFolderPath)
            } else {
                gitService.cloneRepository(localRepositoryFolderPath, duo.key)
            }
        } catch (e: Exception) {
            logger.error(e.message, e)
        }

    }

    private fun getLocalRepositoryFolderName(duo: List<Student>): Path {
        val projectUrl = duo.first().projectUrl
        val usernames = duo.joinToString("-") { it.gitlabUsername }
        val folderName = "${usernames}-${projectUrl.toString().split("/").last()}"
        return Paths.get(folderName)
    }

    private fun extractProjectNameFromGitlabUrl(uri: URI): String {
        val splitUri = uri.toString().split("/")
        return "${splitUri[3]}/${splitUri[4]}"
    }
}