package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

public class ResourceDiscount {
    private ResourceType res;
    private Integer quantity;
    private boolean activated;

    public ResourceDiscount(ResourceType res){
        this.res=res;
        this.quantity=1;
        this.activated=false;
    }
    public void addQuantity(){
        quantity++;
    }

    public Integer getQuantity() { return quantity; }

    public ResourceType getRes() { return res; }

    public boolean isActivated() { return activated; }
    public void toggle(){
        activated=!activated;
    }
}
