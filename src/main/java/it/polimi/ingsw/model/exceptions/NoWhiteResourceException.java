package it.polimi.ingsw.model.exceptions;

public class NoWhiteResourceException extends Exception{
    public NoWhiteResourceException() { super(); }

    @Override
    public String getMessage() {
        return "No white resource in pending";
    }
}
