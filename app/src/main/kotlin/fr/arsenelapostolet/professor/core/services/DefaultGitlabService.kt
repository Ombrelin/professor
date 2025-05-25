package fr.arsenelapostolet.professor.core.services

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import fr.arsenelapostolet.professor.core.application.GitlabService
import fr.arsenelapostolet.professor.core.application.GitlabService.GradedMergeRequestComment
import fr.arsenelapostolet.professor.gitlabgraphql.GradesQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate

class DefaultGitlabService(
    secretService: SecretService,
) : GitlabService {

    val apolloClient = ApolloClient.Builder()
        .serverUrl("https://gitlab.com/api/graphql")
        .addHttpInterceptor(AuthInterceptor(secretService))
        .build()

    override suspend fun getMergeRequestsWithLabelComments(
        projects: List<String>,
        labels: List<String>,
    ): List<GradedMergeRequestComment> {
        val responses = labels.map {
            apolloClient
                .query(
                    GradesQuery(
                        Optional.present(projects),
                        Optional.present("${LocalDate.now().year}-01-01"),
                        Optional.present(listOf(it))
                    )
                )
        }
            .map {
                coroutineScope {
                    async {
                        it.execute()
                    }
                }
            }.awaitAll()

        return responses
            .flatMap { it.data?.projects?.nodes.orEmpty() }
            .map { Pair(it?.fullPath, it?.mergeRequests?.nodes.orEmpty()) }
            .filter { it.second.isNotEmpty() }
            .map { Pair(it.first, it.second.single()) }
            .map {
                val comment =
                    it.second!!.notes.nodes.orEmpty().firstOrNull { note -> note!!.body.contains("Notation") }

                if(comment == null){
                    throw IllegalArgumentException("Le projet ${it.first} a une demande de fusion (${it.second!!.title}) sans commentaire de correction")
                }

                GradedMergeRequestComment(
                    it.first!!,
                    comment.body,
                    it.second?.labels?.nodes.orEmpty().first { label -> label!!.title.contains("livrable") }!!.title
                )
            }
    }

    private class AuthInterceptor(val secretService: SecretService) : HttpInterceptor {
        override suspend fun intercept(
            request: HttpRequest,
            chain: HttpInterceptorChain
        ): HttpResponse {
            return chain
                .proceed(
                    request
                        .newBuilder()
                        .addHeader("Authorization", "Bearer ${secretService["GITLAB_TOKEN"]}")
                        .build()
                )
        }
    }
}