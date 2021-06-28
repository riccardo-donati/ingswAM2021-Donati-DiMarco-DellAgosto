package it.polimi.ingsw.model.exceptions;

public class DepositNotExistingException extends Exception {
    public DepositNotExistingException() { super(); }
    @Override
    public String getMessage() {
        return "Deposit does not exists";
    }
}
