package fr.arsenelapostolet.professor.viewmodels.utils

interface FileService {
    suspend fun pickFile(): String
}