package it.polimi.ingsw.model.exceptions;

public class CardNotAvailableException extends Exception{
    public CardNotAvailableException() { super(); }
    @Override
    public String getMessage() {
        return "Card not available";
    }
}
