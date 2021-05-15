package it.polimi.ingsw.model.exceptions;

public class NonEmptyException extends Exception {
    public NonEmptyException() { super(); }
    public NonEmptyException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Not empty";
    }
}
