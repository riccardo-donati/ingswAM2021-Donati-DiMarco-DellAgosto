package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

public abstract class SpecialAbility {

    protected ResourceType resourceType;


    protected SpecialAbility(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * depending on the class that runs it, activates the special ability for the player
     * @param player affected
     */
    protected void activate(Player player) { }

}
