package fr.arsenelapostolet.professor.core.application

interface GitlabService {

    suspend fun getMergeRequestsWithLabelComments(
        projects: List<String>,
        labels: List<String>,
    ): List<GradedMergeRequestComment>

    data class GradedMergeRequestComment(val mergeRequestAuthorUsername: String, val comment: String, val label: String)

}