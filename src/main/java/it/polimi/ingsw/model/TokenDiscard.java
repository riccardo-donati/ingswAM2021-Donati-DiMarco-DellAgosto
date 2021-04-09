package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.interfaces.Token;

public class TokenDiscard implements Token {
    private Color color;

    @Override
    public void doAction(Game g) {
        g.discardColor(color);
    }
}
