package it.polimi.ingsw.model.exceptions;

public class FullGameException extends Exception {
    public FullGameException() { super(); }
    @Override
    public String getMessage() {
        return "The game is full";
    }
}
