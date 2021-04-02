package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Warehouse {
    private static final Integer DIM=6;

    private ResourceType[] maindepot;
    private List<LeaderDeposit> extradepots;
    private Map<ResourceType,Integer> pendingResources;

    /**
     * initializing the maindepot with empty resourcetypes and
     * the map with the 4 resourcetype we want
     */
    public Warehouse(){
        maindepot=new ResourceType[DIM];
        for(int i=0;i<DIM;i++){
            maindepot[i]=ResourceType.EMPTY;
        }
        extradepots=new ArrayList<>();
        pendingResources=new HashMap<>();
        pendingResources.put(ResourceType.GREY,0);
        pendingResources.put(ResourceType.BLUE,0);
        pendingResources.put(ResourceType.VIOLET,0);
        pendingResources.put(ResourceType.YELLOW,0);
    }

    /**
     *
     * @param res is the resource that i want to add
     * @throws IllegalResourceException if you try to add an illegal resource(RED, UNKNOWN, EMPTY, WHITE)
     */
    public void addResourceInPending(ResourceType res) throws IllegalResourceException {
        if(pendingResources.containsKey(res)){
            pendingResources.replace(res,pendingResources.get(res)+1);
        }else{
            throw new IllegalResourceException();
        }
    }

    /**
     * counts the points generated from the resources in the warehouse and in the leaderdeposits
     * @return the points
     */
    public Integer countWarehousePoints(){
        Integer nres=0;
        for(int i=0;i<DIM;i++){
            if(maindepot[i]!=ResourceType.EMPTY) nres++;
        }
        for(LeaderDeposit ld : extradepots){
            for(int i=0;i<ld.getSpace().length;i++){
                if(ld.getSpace()[i]!=ResourceType.EMPTY) nres++;
            }
        }
        Integer points=nres/5;
        return points;
    }

    /**
     *
     * @param res is the resource of the deposit that i want to create
     * @throws IllegalResourceException if you try to add and illegal deposit
     */
    public void addExtraDepot(ResourceType res) throws IllegalResourceException {
        if(res==ResourceType.RED || res==ResourceType.EMPTY || res==ResourceType.UNKNOWN || res==ResourceType.WHITE){
            throw new IllegalResourceException();
        }else{
            extradepots.add(new LeaderDeposit(res));
        }
    }

    /**
     *
     * @return the list extradepots
     */
    public List<LeaderDeposit> getExtradepots() {
        return extradepots;
    }

    public void addPendingResourcer(){
        //da mettere l'algoritmo per aggiungere le risorse
    }
}
