package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;

public class ExtraDeposit extends SpecialAbility {

    public ExtraDeposit(ResourceType resourceType) {
        super(resourceType);
    }

    /**
     * gives warehouse a resource type to create a new extra deposit
     * @param player affected
     */
    @Override
    public void activate(Player player) {
        try {
            player.getBoard().getWarehouse().addExtraDepot(resourceType);
        } catch (IllegalResourceException e) {
            System.out.println("tried to add extra deposit with illegal resource type");
        }
    }
}
