package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.interfaces.Token;

public class TokenDiscard implements Token {
    private Color color;
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public Color getColor() {
        return color;
    }

    protected TokenDiscard(Color c, Integer qty){
        this.color=c;
        this.quantity=qty;
    }
    @Override
    public void doAction(Game g) {
        for(int i=0;i<quantity;i++)
            g.discardColor(color);
    }
}
