package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.network.client.CLI.enums.Resource;

import java.util.Arrays;

public class Shelf {
    private final Resource[] spaces;
    private final int id;

    public Resource[] getSpaces() { return spaces; }

    public int getId() {
        return id;
    }

    /**
     * count the amount of empty spaces
     * @return the count
     */
    public Integer getEmpty(){
        int count=0;
        for(Resource r: spaces){
            if(r.equals(Resource.EMPTY))
                count++;
        }
        return count;
    }

    /**
     * reset the spaces to EMPTY
     */
    public void clear(){
        Arrays.fill(spaces, Resource.EMPTY);
    }

    /**
     *
     * @param n is the size of the shelf
     * @param id is the id of the shelf
     */
    public Shelf(int n, int id){
        spaces=new Resource[n];
        for(int i=0;i<n;i++){
            spaces[i]=Resource.EMPTY;
        }
        this.id=id;
    }

    public void put(int index, Resource res){
        spaces[index]=res;
    }

    /**
     * remove a resource from a shelf
     * @param index is the index of the resource i want to remove
     * @return the removed resource
     */
    public Resource remove(int index){
        Resource res=spaces[index];
        spaces[index] = Resource.EMPTY;
        return res;
    }
}
