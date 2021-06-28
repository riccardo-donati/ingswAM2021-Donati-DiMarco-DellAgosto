package it.polimi.ingsw.model.exceptions;

public class IllegalLeaderCardsException extends Exception{
    public IllegalLeaderCardsException() { super(); }
    @Override
    public String getMessage() {
        return "Illegal leaders cards";
    }
}
