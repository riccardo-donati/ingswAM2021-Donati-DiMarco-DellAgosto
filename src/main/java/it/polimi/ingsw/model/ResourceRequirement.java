package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.interfaces.Requirement;

public class ResourceRequirement implements Requirement {

    private final Integer quantity;
    private final ResourceType resource;

    public ResourceRequirement(Integer quantity, ResourceType resource) {
        this.quantity = quantity;
        this.resource = resource;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ResourceType getResource() {
        return resource;
    }

    @Override
    public boolean check(Board board) {
        return false;
    }
}
