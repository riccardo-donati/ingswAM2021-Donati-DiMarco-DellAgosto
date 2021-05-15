package it.polimi.ingsw.model.exceptions;

public class IllegalSlotException extends Exception{
    public IllegalSlotException() { super(); }
    public IllegalSlotException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Illegal slot";
    }
}
