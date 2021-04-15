package it.polimi.ingsw.model.exceptions;

public class DepositNotExistingException extends Exception {
    public DepositNotExistingException() { super(); }
    public DepositNotExistingException(String message) { super(message); }
}
