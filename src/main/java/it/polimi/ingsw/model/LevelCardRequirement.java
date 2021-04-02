package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.interfaces.Requirement;

public class LevelCardRequirement implements Requirement {

    private final Color color;
    private final Integer quantity;
    private final Integer level;

    public LevelCardRequirement(Color color, Integer quantity, Integer level) {
        this.color = color;
        this.quantity = quantity;
        this.level = level;
    }

    public Color getColor() {
        return color;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getLevel() {
        return level;
    }

    @Override
    public boolean check(Board board) {
        return false;
    }
}
