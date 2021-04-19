package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;

public class ExtraProduction extends SpecialAbility {

    protected ExtraProduction(ResourceType resourceType) {
        super(resourceType);
    }

    /**
     * creates a new production (resourceType -> UNKNOWN + 1 faith point)
     * and gives it to player to add to the list of extra productions
     * @param player affected
     */
    @Override
    public void activate(Player player) throws IllegalResourceException {
        Production production = new Production();
        production.addInput(resourceType, 1);
        production.addOutput(ResourceType.UNKNOWN, 1);
        production.addOutput(ResourceType.RED, 1);

        player.addExtraProduction(production);
    }
}
