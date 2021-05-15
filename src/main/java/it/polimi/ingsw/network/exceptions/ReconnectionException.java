package it.polimi.ingsw.network.exceptions;

public class ReconnectionException extends Exception{
    @Override
    public String getMessage() {
        return "Reconnected";
    }
}
