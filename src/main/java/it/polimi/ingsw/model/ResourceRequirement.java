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

    public Integer getQuantity() {
        return quantity;
    }

    public ResourceType getResource() {
        return resource;
    }

    @Override
    public boolean check(Board board) {
        Map<ResourceType, Integer> totalResources = board.getStrongBox();
        List<Deposit> tempDepositList = board.getWarehouse().getMaindepot();
        for (Deposit deposit : tempDepositList) {
            int counter = 0;
            if(deposit.getType().equals(resource)) {
                for (ResourceType resourceType : deposit.getSpace())
                    if (!resourceType.equals(ResourceType.EMPTY))
                        counter++;
            }
            totalResources.replace(resource, totalResources.get(resource) + counter);
        }
        tempDepositList = board.getWarehouse().getExtradepots();
        for (Deposit deposit : tempDepositList) {
            int counter = 0;
            if(deposit.getType().equals(resource)) {
                for (ResourceType resourceType : deposit.getSpace())
                    if (!resourceType.equals(ResourceType.EMPTY))
                        counter++;
            }
            totalResources.replace(resource, totalResources.get(resource) + counter);
        }

        return quantity <= totalResources.get(resource);
    }
}
