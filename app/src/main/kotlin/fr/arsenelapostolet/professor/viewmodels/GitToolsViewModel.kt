package fr.arsenelapostolet.professor.viewmodels

import fr.arsenelapostolet.professor.core.application.GitApplication
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
    val localGitDirectory = "/home/arsene/git/adv-java"

    override suspend fun init() {
        var token = secretService["GITLAB_TOKEN"]

        if (token == null) {
            token = dialogService.prompt("Entrez votre token Gitlab")
            secretService["GITLAB_TOKEN"] = token

            if(token == null){
                gitlabTokenAvailable.value = false
                return
            }
        }
        gitlabTokenAvailable.value = true
    }

    suspend fun syncLocalGitRepositories() {
        gitApplication.syncLocalGitRepositories(Paths.get(localGitDirectory))
    }
}