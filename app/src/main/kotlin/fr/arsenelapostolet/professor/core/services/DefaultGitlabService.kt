package fr.arsenelapostolet.professor.core.services

import fr.arsenelapostolet.professor.core.application.GitlabService
import fr.arsenelapostolet.professor.core.application.GitlabService.GradedMergeRequestComment
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.MergeRequestFilter
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*

class DefaultGitlabService(
    secretService: SecretService,
) : GitlabService {

    companion object {
        const val gitlabUrl = "https://gitlab.com"
    }

    private val gitlabApi = GitLabApi(gitlabUrl, secretService["GITLAB_TOKEN"])

    override suspend fun getMergeRequestsWithLabelComments(
        projects: List<String>,
        labels: List<String>,
    ): List<GradedMergeRequestComment> {
        val filter = MergeRequestFilter()
            .withLabels(listOf("Any"))
            .withCreatedAfter(
                Date(
                    LocalDate
                        .now()
                        .minusMonths(4)
                        .atTime(LocalTime.NOON)
                        .toInstant(ZoneOffset.UTC)
                        .toEpochMilli()
                )
            )
        val mergeRequests = gitlabApi
            .mergeRequestApi
            .getMergeRequests(filter)
            .filter { it.labels.any { label -> label in labels } }

        return mergeRequests
            .flatMap {
                gitlabApi
                    .notesApi.getMergeRequestNotes(it.projectId, it.iid)
                    .map { note ->
                        Triple(it.author.username, note, it.labels.filter { label -> label.contains("") })
                    }
                    .filter { it.second.body.contains("Correction livrable") }
                    .map { GradedMergeRequestComment(it.first, it.second.body, it.third.first()) }
            }


    }
}