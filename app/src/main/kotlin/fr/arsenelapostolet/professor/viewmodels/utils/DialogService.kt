package fr.arsenelapostolet.professor.viewmodels.utils

interface DialogService {

    suspend fun prompt(prompt: String): String?

}