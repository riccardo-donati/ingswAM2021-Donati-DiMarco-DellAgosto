package it.polimi.ingsw.model.exceptions;

public class FullGameException extends Exception {
    public FullGameException() { super(); }
    public FullGameException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "The game is full";
    }
}
