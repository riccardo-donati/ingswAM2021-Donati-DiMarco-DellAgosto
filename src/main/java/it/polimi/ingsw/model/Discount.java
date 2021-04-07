package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

public class Discount extends SpecialAbility {

    public Discount(ResourceType resourceType) {
        super(resourceType);
    }

    /**
     * gives player a resource type to add to list of discounts
     * @param player affected
     */
    @Override
    public void activate(Player player) {
        player.addDiscount(resourceType);
    }
}
