package it.polimi.ingsw.model.exceptions;

public class IllegalActionException extends Exception{
    public IllegalActionException() { super(); }
    public IllegalActionException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Illegal Action";
    }
}
