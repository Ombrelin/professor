package fr.arsenelapostolet.professor.core.services

interface SecretService {

    operator fun get(secretName: String): String

}