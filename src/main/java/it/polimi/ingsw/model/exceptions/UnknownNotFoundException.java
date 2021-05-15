package it.polimi.ingsw.model.exceptions;

public class UnknownNotFoundException extends Exception {
    public UnknownNotFoundException() { super(); }
    public UnknownNotFoundException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Unknown resource not found";
    }
}
