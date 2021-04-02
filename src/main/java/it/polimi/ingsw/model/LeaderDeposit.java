package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;

public class LeaderDeposit {
    private static final Integer DIM=2;
    private ResourceType type;
    private ResourceType[] space;

    /**
     * initialize the array called space with EMPTY resources
     * @param type of the LeaderDeposit
     */
    public LeaderDeposit(ResourceType type){
        this.type=type;
        space=new ResourceType[DIM];
        for(int i=0;i<DIM;i++){
            space[i]=ResourceType.EMPTY;
        }
    }

    /**
     * get of space
     * @return the space array
     */
    public ResourceType[] getSpace() {
        return space;
    }

    /**
     * get of type
     * @return the type of the deposit
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * Add a ResourceType in the space
     * @param res the resource that i want to add
     * @param index the index where i want to add
     * @throws IllegalResourceException if the ResourceType is wrong
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public void addResource(ResourceType res,Integer index) throws IllegalResourceException,IndexOutOfBoundsException {
        if(res!=type) throw new IllegalResourceException();
        if(index<0 || index>DIM-1) throw new IndexOutOfBoundsException();
        space[index]=res;
    }
}
