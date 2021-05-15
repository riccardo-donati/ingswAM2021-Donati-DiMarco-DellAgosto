package it.polimi.ingsw.model.exceptions;

public class ResourcesNotAvailableException extends Exception{
    public ResourcesNotAvailableException() { super(); }
    public ResourcesNotAvailableException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Resources not available";
    }
}
