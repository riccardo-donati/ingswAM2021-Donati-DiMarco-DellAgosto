package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

public class WhiteTo extends SpecialAbility{

    public WhiteTo(ResourceType resourceType) {
        super(resourceType);
    }

    /**
     * gives player a resource type to add to the list of white conversions
     * @param player affected
     */
    @Override
    public void activate(Player player) {
        player.addWhiteTo(resourceType);
    }
}
