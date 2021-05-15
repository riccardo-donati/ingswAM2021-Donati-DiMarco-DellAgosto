package it.polimi.ingsw.model.exceptions;

public class NoWhiteResourceException extends Exception{
    public NoWhiteResourceException() { super(); }
    public NoWhiteResourceException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "No white resource in pending";
    }
}
