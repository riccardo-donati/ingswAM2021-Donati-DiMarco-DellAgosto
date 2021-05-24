package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullSpaceException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;

import java.util.Arrays;

public class Deposit {
    @Expose
    private Integer id;
    @Expose
    private Integer dim;
    @Expose
    private ResourceType type;
    @Expose
    private ResourceType[] space;

    /**
     * initialize the array called space with EMPTY resources
     */
    protected Deposit(Integer id,ResourceType res,Integer dim) throws IllegalResourceException {
        if (dim < 0) throw new NegativeArraySizeException();
        if (res == ResourceType.WHITE || res == ResourceType.UNKNOWN) throw new IllegalResourceException();
        this.id = id;
        this.type = res;
        this.dim = dim;
        space = new ResourceType[this.dim];
        Arrays.fill(space, ResourceType.EMPTY);
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
    protected Integer getDimension() {
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
    protected ResourceType getType() {
        return type;
    }

    /**
     * Add a ResourceType in the space
     * @param res the resource that i want to add
     * @throws IllegalResourceException if the ResourceType is wrong
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    protected void addResource(ResourceType res) throws IllegalResourceException, IndexOutOfBoundsException, FullSpaceException {
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
    protected ResourceType removeResource() {
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
    protected void changeType(ResourceType res) throws NonEmptyException {
        for(int i=0;i<dim;i++){
            if(space[i]!=ResourceType.EMPTY){
                throw new NonEmptyException();
            }
        }
        type=res;
    }
    protected void visualize(){
        System.out.println("ID:"+id);
        System.out.println("TYPE:"+type);
        System.out.print("SPACE:");
        for(int i=0;i<dim;i++){
            System.out.print(space[i]+"|");
        }
        System.out.print("\n");
    }

    /**
     * @return the number of EMPTY spaces in the deposit
     */
    protected Integer freeSpaces(){
        Integer count=0;
        for(int i=0;i<dim;i++){
            if(space[i]==ResourceType.EMPTY) count++;
        }
        return count;
    }
}
