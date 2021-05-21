package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.interfaces.Token;

public class TokenPush implements Token {
    @Expose
    private Integer quantity;
    protected TokenPush(Integer qty){
        this.quantity=qty;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void doAction(Game g) {
        g.pushBlackCross(quantity);
    }
}
