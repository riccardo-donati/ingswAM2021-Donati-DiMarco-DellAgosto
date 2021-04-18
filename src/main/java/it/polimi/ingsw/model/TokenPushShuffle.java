package it.polimi.ingsw.model;

import it.polimi.ingsw.model.interfaces.Token;

public class TokenPushShuffle implements Token {
    private Integer quantity;
    public TokenPushShuffle(Integer qty){
        quantity=qty;
    }

    @Override
    public void doAction(Game g) {
        g.pushBlackCross(quantity);
        g.tokenShuffle();
    }
}
