package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

public abstract class SpecialAbility {

    protected ResourceType resourceType;


    public SpecialAbility(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * depending on the class that runs it, activates the special ability for the player
     * @param player affected
     */
    public void activate(Player player) { }

}
