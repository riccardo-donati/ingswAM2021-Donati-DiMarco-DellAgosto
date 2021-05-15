package it.polimi.ingsw.model.exceptions;

public class UnknownFoundException extends Exception {

    public UnknownFoundException() { super(); }
    public UnknownFoundException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Unknown resource found";
    }
}
