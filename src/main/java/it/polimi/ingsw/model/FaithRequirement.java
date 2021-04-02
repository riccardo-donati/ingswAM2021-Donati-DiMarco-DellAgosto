package it.polimi.ingsw.model;

import it.polimi.ingsw.model.interfaces.Requirement;

public class FaithRequirement implements Requirement {

    private final Integer position;

    public FaithRequirement(Integer position) {
        this.position = position;
    }

    public Integer getPosition() {
        return position;
    }

    @Override
    public boolean check(Board board) {
        return false;
    }
}
