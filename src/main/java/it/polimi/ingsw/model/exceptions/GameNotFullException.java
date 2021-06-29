package it.polimi.ingsw.model.exceptions;

public class GameNotFullException extends Exception{
    public GameNotFullException() { super(); }
    @Override
    public String getMessage() {
        return "Game not found";
    }
}
