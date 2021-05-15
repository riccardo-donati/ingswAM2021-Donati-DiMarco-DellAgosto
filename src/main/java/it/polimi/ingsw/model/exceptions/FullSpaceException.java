package it.polimi.ingsw.model.exceptions;

public class FullSpaceException extends Exception {
    public FullSpaceException() { super(); }
    public FullSpaceException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "The space is full";
    }
}
