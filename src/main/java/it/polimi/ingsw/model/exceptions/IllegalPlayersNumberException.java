package it.polimi.ingsw.model.exceptions;

public class IllegalPlayersNumberException extends Exception{
    public IllegalPlayersNumberException() { super(); }
    @Override
    public String getMessage() {
        return "Illegal number of players";
    }
}
