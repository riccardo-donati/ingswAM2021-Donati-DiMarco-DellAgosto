package it.polimi.ingsw.network.exceptions;

public class NotYourTurnException extends Exception{
    @Override
    public String getMessage() {
        return "It's not your turn";
    }
}
