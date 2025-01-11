package fr.arsenelapostolet.professor.core.services

import de.swiesend.secretservice.simple.SimpleCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FreeDesktopSecretService() : SecretService {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(FreeDesktopSecretService::class.java)
    }

    override fun get(secretName: String): String? {
        val simpleCollection = SimpleCollection()
        val item = simpleCollection.findItem(secretName)

        if (item != null) {
            val secret = simpleCollection.getSecret(item)
            if (secret != null) {
                logger.info("Freedesktop secret \"$secretName\" found at : $item")
                return String(secret)
            }
        }
        logger.info("Freedesktop secret \"$secretName\" not found")
        return null;
    }

    private fun SimpleCollection.findItem(secretName: String): String? =
        this
            .getItems(mapOf<String, String>())
            .first { this.getLabel(it) == secretName }

    override fun set(secretName: String, secretValue: String?) {
        val item = SimpleCollection().createItem(secretName, secretValue)
        logger.info("Freedesktop secret \"$secretName\" stored at : $item")
    }
}