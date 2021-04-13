package it.polimi.ingsw.model;

import it.polimi.ingsw.model.interfaces.Marble;

public class RedMarble implements Marble {

    /**
     * increases the player's position on the faith path by one
     * @param p target of the changes
     */
    @Override
    public void action(Player p) {
        p.getBoard().getFaithPath().addToPosition(1);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RedMarble;
    }
}
