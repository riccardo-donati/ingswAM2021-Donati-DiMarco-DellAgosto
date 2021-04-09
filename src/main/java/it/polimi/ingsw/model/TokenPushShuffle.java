package it.polimi.ingsw.model;

import it.polimi.ingsw.model.interfaces.Token;

public class TokenPushShuffle implements Token {

    @Override
    public void doAction(Game g) {
        g.pushBlackCross(1);
        g.tokenShuffle();
    }
}
