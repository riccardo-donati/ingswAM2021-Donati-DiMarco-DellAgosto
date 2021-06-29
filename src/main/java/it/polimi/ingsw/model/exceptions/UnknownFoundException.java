package it.polimi.ingsw.model.exceptions;

public class UnknownFoundException extends Exception {

    public UnknownFoundException() { super(); }
    @Override
    public String getMessage() {
        return "Unknown resource found";
    }
}
