package it.polimi.ingsw.model.exceptions;

public class IllegalLeaderCardsException extends Exception{
    public IllegalLeaderCardsException() { super(); }
    public IllegalLeaderCardsException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Illegal leaders cards";
    }
}
