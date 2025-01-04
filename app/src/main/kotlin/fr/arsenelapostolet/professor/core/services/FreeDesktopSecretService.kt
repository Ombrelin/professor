package fr.arsenelapostolet.professor.core.services

import de.swiesend.secretservice.simple.SimpleCollection

class FreeDesktopSecretService : SecretService {
    override fun get(secretName: String): String? {
        val simpleCollection = SimpleCollection()
        val item = simpleCollection.findItem(secretName)

        if(item != null) {
            val secret = simpleCollection.getSecret(item)
            if(secret != null) {
                return String(secret)
            }
        }
       return null;
    }

    private fun SimpleCollection.findItem(
        secretName: String,
    ): String? {
        return  this
            .getItems(mapOf<String, String>())
            .first { this.getLabel(it) == secretName }
    }

    override fun set(secretName: String, secretValue: String?) {
        SimpleCollection().createItem(secretName, secretValue)
    }
}