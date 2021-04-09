package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;

public class ExtraDeposit extends SpecialAbility {

    public ExtraDeposit(ResourceType resourceType) {
        super(resourceType);
    }

    @Override
    public void activate(Player player) {
        try {
            player.getBoard().getWarehouse().addExtraDepot(resourceType);
        } catch (IllegalResourceException e) {
            System.out.println("tried to add extra deposit with illegal resource type");
        }
    }
}
