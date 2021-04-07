package it.polimi.ingsw.model.exceptions;

public class CardNotAvailableException extends Exception{
    public CardNotAvailableException() { super(); }
    public CardNotAvailableException(String message) { super(message); }
}
