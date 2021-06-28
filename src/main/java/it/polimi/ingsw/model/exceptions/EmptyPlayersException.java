package it.polimi.ingsw.model.exceptions;

public class EmptyPlayersException extends  Exception{
    public EmptyPlayersException() { super(); }
    @Override
    public String getMessage() {
        return "Empty players";
    }
}
