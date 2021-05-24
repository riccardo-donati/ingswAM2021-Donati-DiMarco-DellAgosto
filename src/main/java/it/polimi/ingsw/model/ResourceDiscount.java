package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.enums.ResourceType;

public class ResourceDiscount {
    @Expose
    private ResourceType res;
    @Expose
    private Integer quantity;
    @Expose
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
