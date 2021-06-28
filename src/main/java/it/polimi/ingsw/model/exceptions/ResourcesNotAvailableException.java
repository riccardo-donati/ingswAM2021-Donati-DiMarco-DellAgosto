package it.polimi.ingsw.model.exceptions;

public class ResourcesNotAvailableException extends Exception{
    public ResourcesNotAvailableException() { super(); }
    @Override
    public String getMessage() {
        return "Resources not available";
    }
}
