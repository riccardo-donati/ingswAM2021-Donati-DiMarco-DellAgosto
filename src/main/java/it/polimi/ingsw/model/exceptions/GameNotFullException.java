package it.polimi.ingsw.model.exceptions;

public class GameNotFullException extends Exception{
    public GameNotFullException() { super(); }
    public GameNotFullException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Game not found";
    }
}
