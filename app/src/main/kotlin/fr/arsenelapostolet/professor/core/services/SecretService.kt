package fr.arsenelapostolet.professor.core.services

interface SecretService {

    operator fun get(secretName: String): String?
    operator fun set(secretName: String, value: String?)
}