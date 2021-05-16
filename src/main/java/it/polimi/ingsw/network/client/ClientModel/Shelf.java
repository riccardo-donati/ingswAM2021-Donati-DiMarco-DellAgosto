package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;

public class Shelf {
    private String[] spaces;
    private int n;
    private int id;

    public String[] getSpaces() {
        return spaces;
    }

    public int getId() {
        return id;
    }
    public void clear(){
        for(int i=0;i<spaces.length;i++){
            spaces[i]="  ";
        }
    }

    public Shelf(int n, int id){
        spaces=new String[n];
        this.id=id;
    }

    public void put(int index, Resource res){
        spaces[index]=res.label;
    }

    public void remove(int index){
        spaces[index] = "  ";
    }
}
