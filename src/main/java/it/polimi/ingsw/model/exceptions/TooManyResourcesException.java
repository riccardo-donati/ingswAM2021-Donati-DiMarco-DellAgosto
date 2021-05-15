package it.polimi.ingsw.model.exceptions;

public class TooManyResourcesException extends Exception {
    public TooManyResourcesException() { super(); }
    public TooManyResourcesException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Too many resources in hand";
    }
}
