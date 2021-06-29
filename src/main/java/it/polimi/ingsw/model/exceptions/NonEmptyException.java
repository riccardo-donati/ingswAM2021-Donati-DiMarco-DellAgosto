package it.polimi.ingsw.model.exceptions;

public class NonEmptyException extends Exception {
    public NonEmptyException() { super(); }
    @Override
    public String getMessage() {
        return "Not empty";
    }
}
