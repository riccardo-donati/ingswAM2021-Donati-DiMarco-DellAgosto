package it.polimi.ingsw.model.exceptions;

public class RequirementNotMetException extends Exception{
    public RequirementNotMetException() { super(); }
    public RequirementNotMetException(String message) { super(message); }

    @Override
    public String getMessage() {
        return "Requirements not met";
    }
}