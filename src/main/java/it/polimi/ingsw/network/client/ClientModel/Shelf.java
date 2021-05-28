package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;

public class Shelf {
    private Resource[] spaces;
    private int n;
    private int id;

    public Resource[] getSpaces() { return spaces; }

    public int getId() {
        return id;
    }

    public Integer getEmpty(){
        int count=0;
        for(Resource r: spaces){
            if(r.equals(Resource.EMPTY))
                count++;
        }
        return count;
    }

    public void clear(){
        for(int i=0;i<spaces.length;i++){
            spaces[i]=Resource.EMPTY;
        }
    }

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

    public Resource remove(int index){
        Resource res=spaces[index];
        spaces[index] = Resource.EMPTY;
        return res;
    }
}
