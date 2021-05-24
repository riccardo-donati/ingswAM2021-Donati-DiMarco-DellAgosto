package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.interfaces.Requirement;

public class FaithRequirement implements Requirement {
    @Expose
    private final Integer position;

    protected FaithRequirement(Integer position) {
        this.position = position;
    }

    /**
     *
     * @return the requirement position
     */
    protected Integer getPosition() {
        return position;
    }

    /**
     * retrieves the faith path from the board and checks the position
     * @param board board that needs to be checked
     * @return true if the position on the faith path is at least "position", false otherwise
     */
    @Override
    public boolean check(Board board) {
        return position <= board.getFaithPath().getPosition();
    }
}
