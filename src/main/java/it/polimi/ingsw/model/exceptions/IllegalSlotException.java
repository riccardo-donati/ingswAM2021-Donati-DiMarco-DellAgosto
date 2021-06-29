package it.polimi.ingsw.model.exceptions;

public class IllegalSlotException extends Exception{
    public IllegalSlotException() { super(); }
    @Override
    public String getMessage() {
        return "Illegal slot";
    }
}
