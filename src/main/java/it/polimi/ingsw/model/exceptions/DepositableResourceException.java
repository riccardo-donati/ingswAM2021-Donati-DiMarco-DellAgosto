package it.polimi.ingsw.model.exceptions;

public class DepositableResourceException extends Exception {
    public DepositableResourceException() { super(); }
    public DepositableResourceException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "There is a depositable resource";
    }
}
