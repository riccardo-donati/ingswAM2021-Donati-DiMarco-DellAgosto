package it.polimi.ingsw.model.exceptions;

public class DepositableResourceException extends Exception {
    public DepositableResourceException() { super(); }
    @Override
    public String getMessage() {
        return "There is a depositable resource";
    }
}
