package it.polimi.ingsw.model.exceptions;

public class IllegalResourceException extends Exception{
    public IllegalResourceException() { super(); }
    public IllegalResourceException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Illegal resource";
    }
}
