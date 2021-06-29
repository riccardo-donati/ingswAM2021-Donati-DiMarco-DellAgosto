package it.polimi.ingsw.model.exceptions;

public class UnknownNotFoundException extends Exception {
    public UnknownNotFoundException() { super(); }
    @Override
    public String getMessage() {
        return "Unknown resource not found";
    }
}
