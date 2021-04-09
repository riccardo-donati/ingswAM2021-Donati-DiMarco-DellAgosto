package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullSpaceException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;

public class Deposit {
    private static int ndeposit=0;
    private Integer id;
    private Integer dim;
    private ResourceType type;
    private ResourceType[] space;

    /**
     * initialize the array called space with EMPTY resources
     */
    public Deposit(ResourceType res,Integer dim) throws IllegalResourceException {
        if(dim<0) throw new NegativeArraySizeException();
        if(res==ResourceType.WHITE || res==ResourceType.UNKNOWN) throw new IllegalResourceException();
        ndeposit++;
        this.id=ndeposit;
        this.type=res;
        this.dim=dim;
        space=new ResourceType[dim];
        for(int i=0;i<dim;i++){
            space[i]=ResourceType.EMPTY;
        }
    }

    /**
     * get of id
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * get of dimension
     * @return the length of the array
     */
    public Integer getDimension() {
        return dim;
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
     * @throws IllegalResourceException if the ResourceType is wrong
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public void addResource(ResourceType res) throws IllegalResourceException, IndexOutOfBoundsException, FullSpaceException {
        if(res==ResourceType.WHITE || res==ResourceType.RED || res==ResourceType.UNKNOWN) throw new IllegalResourceException();
        if(type!=ResourceType.EMPTY) {
            if (res != type) throw new IllegalResourceException();
            for(int i=0;i<dim;i++) {
                if (space[i] == ResourceType.EMPTY) {
                    space[i] = res;
                    return;
                }
            }
        }else{
                for (int i = 0; i < dim; i++) {
                    if (space[i] == ResourceType.EMPTY) {
                        space[i] = res;
                        type=res;
                        return;
                    }
                }
            }
        throw new FullSpaceException();
    }

    /**
     *
     * @return the removedResource
     */
    public ResourceType removeResource() {
        ResourceType ret=ResourceType.EMPTY;
        for(int i=dim-1;i>=0;i--){
            if(space[i]!=ResourceType.EMPTY){
                ret=space[i];
                space[i]=ResourceType.EMPTY;
                break;
            }
        }
        //Check if space is empty
        try {
            changeType(ResourceType.EMPTY);
        } catch (NonEmptyException e) {
            //do nothing
        }
        return ret;
    }
    public void changeType(ResourceType res) throws NonEmptyException {
        for(int i=0;i<dim;i++){
            if(space[i]!=ResourceType.EMPTY){
                throw new NonEmptyException();
            }
        }
        type=res;
    }
    public void visualize(){
        System.out.println("ID:"+id);
        System.out.println("TYPE:"+type);
        System.out.print("SPACE:");
        for(int i=0;i<dim;i++){
            System.out.print(space[i]+"|");
        }
        System.out.print("\n");
    }
}
