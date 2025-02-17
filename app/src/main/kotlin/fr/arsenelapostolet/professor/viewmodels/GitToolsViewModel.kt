package fr.arsenelapostolet.professor.viewmodels

import fr.arsenelapostolet.professor.core.application.GitApplication
import fr.arsenelapostolet.professor.core.application.StorageService
import fr.arsenelapostolet.professor.core.services.SecretService
import fr.arsenelapostolet.professor.viewmodels.utils.DialogService
import fr.arsenelapostolet.professor.viewmodels.utils.ViewModel
import fr.arsenelapostolet.professor.viewmodels.utils.ViewModelProperty
import java.nio.file.Paths

class GitToolsViewModel(
    private val gitApplication: GitApplication,
    private val secretService: SecretService,
    private val dialogService: DialogService,
) : ViewModel {

    val gitlabTokenAvailable = ViewModelProperty<Boolean>(false)
    val refreshProgress = ViewModelProperty<Double>(0.0)
    val repositoriesDirectory = Paths.get("/home/arsene/git/adv-java-student-projects")

    override suspend fun init() {
        var token = secretService["GITLAB_TOKEN"]
        gitlabTokenAvailable.value = token != null
    }

    suspend fun updateGitlabToken() {
        var token = dialogService.prompt("Entrez votre token Gitlab")
        secretService["GITLAB_TOKEN"] = token

        gitlabTokenAvailable.value = token != null
    }

    suspend fun syncLocalGitRepositories() {
        gitApplication.synchronizeLocalGitRepositories(repositoriesDirectory, refreshProgress)
    }
}