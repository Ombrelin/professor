package fr.arsenelapostolet.professor.core.services

import fr.arsenelapostolet.professor.core.application.GitlabService
import org.gitlab4j.api.GitLabApi

class DefaultGitlabService(secretService: SecretService) : GitlabService {

    private val token = secretService["GITLAB_TOKEN"]
    private val gitlabApi = GitLabApi("https://gitlab.com", token)

    override suspend fun getMergeRequestsWithLabelComments(
        projects: List<String>,
        label: String,
    ): List<String> {
        TODO("Not yet implemented")
    }

}