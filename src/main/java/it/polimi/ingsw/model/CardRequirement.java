package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.interfaces.Requirement;

public class CardRequirement implements Requirement {

    private final Color color;
    private final Integer quantity;

    public CardRequirement(Color color, Integer quantity) {
        this.color = color;
        this.quantity = quantity;
    }

    public Color getColor() {
        return color;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public boolean check(Board board) {
        return false;
    }
}
