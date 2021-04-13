package it.polimi.ingsw.model.interfaces;

import it.polimi.ingsw.model.Player;

public interface Marble {

    /**
     * effect of the marble
     * @param p target of the changes
     */
    void action(Player p);
}
