package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.interfaces.Requirement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceRequirement implements Requirement {

    private final Integer quantity;
    private final ResourceType resource;

    public ResourceRequirement(ResourceType resource, Integer quantity) {
        this.quantity = quantity;
        this.resource = resource;
    }

    /**
     *
     * @return the requirement quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     *
     * @return the requirement resource type
     */
    public ResourceType getResource() {
        return resource;
    }

    /**
     * checks all the resources the player has
     * @param board board that needs to be checked
     * @return true if the player has at least "quantity" "resource"
     */
    @Override
    public boolean check(Board board) {
        Map<ResourceType, Integer> totalResources = board.getStrongBox();
        Player.mergeResourceTypeMaps(totalResources, board.getWarehouse().getTotalResources());

        return quantity <= totalResources.get(resource);
    }
}
