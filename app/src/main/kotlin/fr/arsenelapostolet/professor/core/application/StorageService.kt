package fr.arsenelapostolet.professor.core.application

import java.nio.file.Path

interface StorageService {

    fun getStorageDirectoryPath(): Path

}