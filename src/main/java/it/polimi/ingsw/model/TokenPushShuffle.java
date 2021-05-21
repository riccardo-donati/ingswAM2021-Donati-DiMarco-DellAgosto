package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.interfaces.Token;

public class TokenPushShuffle implements Token {
    @Expose
    private Integer quantity;
    public TokenPushShuffle(Integer qty){
        quantity=qty;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void doAction(Game g) {
        g.pushBlackCross(quantity);
        g.tokenShuffle();
    }
}
