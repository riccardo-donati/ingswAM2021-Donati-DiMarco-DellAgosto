package it.polimi.ingsw.model.exceptions;

public class RequirementNotMetException extends Exception{
    public RequirementNotMetException() { super(); }
    @Override
    public String getMessage() {
        return "Requirements not met";
    }
}