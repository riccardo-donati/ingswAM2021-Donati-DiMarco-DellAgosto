package it.polimi.ingsw.model.exceptions;

public class IllegalActionException extends Exception{
    public IllegalActionException() { super(); }
    @Override
    public String getMessage() {
        return "Illegal Action";
    }
}
