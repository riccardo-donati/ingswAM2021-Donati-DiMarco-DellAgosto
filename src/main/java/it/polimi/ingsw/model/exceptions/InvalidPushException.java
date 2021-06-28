package it.polimi.ingsw.model.exceptions;

public class InvalidPushException extends Exception {
    public InvalidPushException() { super(); }
    @Override
    public String getMessage() {
        return "Invalid push";
    }
}
