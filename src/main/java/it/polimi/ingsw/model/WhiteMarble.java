package it.polimi.ingsw.model;

import it.polimi.ingsw.model.interfaces.Marble;

public class WhiteMarble implements Marble {
    @Override
    public void action(Player p) {

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WhiteMarble;
    }
}
