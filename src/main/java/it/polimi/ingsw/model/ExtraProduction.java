package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class ExtraProduction extends SpecialAbility {

    public ExtraProduction(ResourceType resourceType) {
        super(resourceType);
    }

    /**
     * creates a new production (resourceType -> UNKNOWN + 1 faith point)
     * and gives it to player to add to list of extra productions
     * @param player affected
     */
    @Override
    public void activate(Player player) {
        Production production = new Production();
        Map<ResourceType, Integer> input = new HashMap<>();
        Map<ResourceType, Integer> output = new HashMap<>();
        input.put(resourceType, 1);
        output.put(ResourceType.UNKNOWN, 1);
        output.put(ResourceType.RED, 1);

        production.setInput(input);
        production.setOutput(output);

        player.addExtraProduction(production);
    }
}
