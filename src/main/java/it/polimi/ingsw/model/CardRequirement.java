package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.interfaces.Requirement;

import java.util.Map;
import java.util.Stack;

public class CardRequirement implements Requirement {

    private final Color color;
    private final Integer quantity;

    /**
     * constructor
     * @param color required card's color
     * @param quantity required card's quantity
     */
    protected CardRequirement(Color color, Integer quantity) {
        this.color = color;
        this.quantity = quantity;
    }

    /**
     * @return the requirement color
     */
    protected Color getColor() {
        return color;
    }

    /**
     *
     * @return the requirement quantity
     */
    protected Integer getQuantity() {
        return quantity;
    }

    /**
     * checks the development cards on the board looking for a specific color and quantity
     * @param board board that needs to be checked
     * @return true if the board has at least "quantity" of development cards of color "color", false otherwise
     */
    @Override
    public boolean check(Board board) {
        int counter = 0;
        Map<Integer, Stack<DevelopmentCard>> slots = board.getSlots();

        for(Integer integer : slots.keySet()) {
            for(DevelopmentCard developmentCard : slots.get(integer)) {
                if (developmentCard.getColor().equals(color))
                    counter++;
            }
        }

        return quantity <= counter;
    }
}
