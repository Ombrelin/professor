package fr.arsenelapostolet.professor.fakes

import fr.arsenelapostolet.professor.core.services.SecretService



class FakeSecretService : SecretService {

    private val secrets = mutableMapOf<String, String?>()

    override fun get(secretName: String): String? {
        return secrets[secretName]
    }

    override fun set(secretName: String, value: String?) {
        secrets[secretName] = value
    }
}