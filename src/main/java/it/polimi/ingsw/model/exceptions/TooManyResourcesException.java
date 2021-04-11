package it.polimi.ingsw.model.exceptions;

public class TooManyResourcesException extends Exception {
    public TooManyResourcesException() { super(); }
    public TooManyResourcesException(String message) { super(message); }
}
