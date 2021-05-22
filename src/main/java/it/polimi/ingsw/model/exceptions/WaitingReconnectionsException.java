package it.polimi.ingsw.model.exceptions;

public class WaitingReconnectionsException extends Exception {
    @Override
    public String getMessage() {
        return "Waiting for others players reconnection after server crash";
    }
}
