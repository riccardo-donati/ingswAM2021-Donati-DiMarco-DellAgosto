package it.polimi.ingsw.model;

import it.polimi.ingsw.model.interfaces.Marble;

public class RedMarble implements Marble {
    @Override
    public void action(Player p) {
        p.getBoard().getFaithPath().addToPosition(1);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RedMarble;
    }
}
