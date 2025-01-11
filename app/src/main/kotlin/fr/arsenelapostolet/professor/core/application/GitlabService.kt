package fr.arsenelapostolet.professor.core.application

interface GitlabService {

    suspend fun getMergeRequestsWithLabelComments(projects: List<String>, label: String): List<String>

}