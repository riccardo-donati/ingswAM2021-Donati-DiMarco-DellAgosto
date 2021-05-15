package it.polimi.ingsw.model.exceptions;

public class InvalidPushException extends Exception {
    public InvalidPushException() { super(); }
    public InvalidPushException(String message) { super(); }

    @Override
    public String getMessage() {
        return "Invalid push";
    }
}
