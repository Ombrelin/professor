package fr.arsenelapostolet.professor.core.exceptions

class InvalidResourceException : RuntimeException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(message: String?) : super(message)

}