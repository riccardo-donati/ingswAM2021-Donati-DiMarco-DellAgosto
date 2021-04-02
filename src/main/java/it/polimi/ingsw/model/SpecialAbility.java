package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

public abstract class SpecialAbility {

    private ResourceType resource;

    /**
     * depending on the class that runs it, activates the special ability for the player
     * @param player affected
     */
    public void activate(Player player) { }

}
