package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

public class ResourceDiscount {
    private ResourceType res;
    private Integer quantity;
    private boolean activated;

    protected ResourceDiscount(ResourceType res){
        this.res=res;
        this.quantity=1;
        this.activated=false;
    }
    protected void addQuantity(){
        quantity++;
    }

    protected Integer getQuantity() { return quantity; }

    protected ResourceType getRes() { return res; }

    protected boolean isActivated() { return activated; }
    protected void toggle(){
        activated=!activated;
    }
}
