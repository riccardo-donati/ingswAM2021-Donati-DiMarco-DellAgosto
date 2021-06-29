package it.polimi.ingsw.model.exceptions;

public class TooManyResourcesException extends Exception {
    public TooManyResourcesException() { super(); }
    @Override
    public String getMessage() {
        return "Too many resources in hand";
    }
}
