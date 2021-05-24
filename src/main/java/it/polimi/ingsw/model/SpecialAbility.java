package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;

public abstract class SpecialAbility {
    @Expose
    protected ResourceType resourceType;


    public ResourceType getResourceType() {
        return resourceType;
    }

    protected SpecialAbility(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * depending on the class that runs it, activates the special ability for the player
     * @param player affected
     */
    protected void activate(Player player) throws IllegalResourceException { }

}
