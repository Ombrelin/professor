package fr.arsenelapostolet.professor.server.core.exceptions;

public class InvalidResourceException extends RuntimeException {
    public InvalidResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
