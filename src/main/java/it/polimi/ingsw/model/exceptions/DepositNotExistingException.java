package it.polimi.ingsw.model.exceptions;

public class DepositNotExistingException extends Exception {
    public DepositNotExistingException() { super(); }
    public DepositNotExistingException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Deposit does not exists";
    }
}
