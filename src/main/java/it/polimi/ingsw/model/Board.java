package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.Source;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.InvalidPushException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;
import it.polimi.ingsw.model.interfaces.BoardObserver;

import java.io.InvalidObjectException;
import java.util.*;

public class Board {
    private Map<ResourceType, Integer> strongbox;
    private Warehouse warehouse;
    private FaithPath faithpath;
    private Production baseProduction;
    private Map<Integer, Stack<DevelopmentCard>> slots;
    private List<BoardObserver> observers = new ArrayList<>();

    public Board() {
        strongbox = new HashMap<>();
        strongbox.put(ResourceType.BLUE, 0);
        strongbox.put(ResourceType.GREY, 0);
        strongbox.put(ResourceType.VIOLET, 0);
        strongbox.put(ResourceType.YELLOW, 0);
        this.warehouse = new Warehouse();
        this.faithpath = new FaithPath();
        this.baseProduction = new Production();
        baseProduction.addInput(ResourceType.UNKNOWN,2);
        baseProduction.addOutput(ResourceType.UNKNOWN,1);
        slots = new HashMap<>();
        slots.put(1, new Stack<>());
        slots.put(2, new Stack<>());
        slots.put(3, new Stack<>());
    }

    /**
     * the function gives a map with a resource type and the relative number ad adds it to the player's strogbox
     * previously initialized to 0, it increments the relative key in the map
     * @param prod acquired resources from the production
     */
    public void depositInStrongbox(Map<ResourceType, Integer> prod){
        for(ResourceType res : prod.keySet()) {
            for (Map.Entry<ResourceType, Integer> entry : strongbox.entrySet()) {
                if (res.equals(entry.getKey())) {
                    Integer curr = entry.getValue() + prod.get(res);
                    strongbox.replace(res, curr);
                }
            }
        }
    }

    /**
     * getter of the board's strongbox
     * @return the strongbox
     */
    public Map<ResourceType, Integer> getStrongBox(){
        return strongbox;
    }

    public Production getBaseProduction() {
        return baseProduction;
    }

    /**
     * selects 2 resources from warehouse's maindepot using the relative funcion and stores in the strongbox a chosen resource
     * @param input 2 any kind of resources from the main depot in the warehouse
     * @param out one resource of any kind to store in the strongbox
     */
    public void setBaseProduction(ResourceType[] input, ResourceType out){

        for (ResourceType resourceType : input)
            baseProduction.addInput(resourceType, 1);

        baseProduction.addOutput(out, 1);
    }


    /**
     * add the observer in a list of observer
     * @param obs the observer that has to be added in the observer's list
     */
    public void addObserver(BoardObserver obs){observers.add(obs);}

    /**
     * getter function for the player's warehouse
     * @return the player's warehouse
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * notify the observer when QUALCOSA SUCCEDE MA NON SO COSA
     */
    public void notifyObserver(){
        for(BoardObserver obs : observers){
            obs.updateEndGame();
        }
    }

    /**
     * picks Resources from any storage specified by the variable Source
     * @param res the type of the chosen resource
     * @param source where is it taken from
     */
    public void pickUpResource(ResourceType res, Source source){}

    /**
     * counts all the points of a player's board, from cards to faith path
     * @return the current player score given by the sum of all the board's points
     */
    public Integer countBoardsPoints(){
        Integer score = 0;
        for (Map.Entry<Integer,Stack<DevelopmentCard>> entry : slots.entrySet()){
            for (DevelopmentCard dev : entry.getValue()){
                score += dev.getPoints();
            }
        }
        int nres=warehouse.countWarehouseResource();
        for (Map.Entry<ResourceType,Integer> entry : strongbox.entrySet()) {
            nres+=entry.getValue();
        }
        score += nres/5+ faithpath.countFaithPoints();
        return score;       //i punti dati dalle leader cards li somma direttamente la funzione chiamante
    }
    public Map<Integer, Stack<DevelopmentCard>> getSlots() {
        return slots;
    }
    /**
     * pushes a card into a specified position
     * @param pos the position where the cards sholud be added
     * @param card the development card that has to be added
     */
    public void pushDCard (Integer pos, DevelopmentCard card) throws NonEmptyException, InvalidPushException {
        if(card.getLevel()==1){
            if(slots.get(pos).size()==0){
                slots.get(pos).push(card);
            }else{
                throw new InvalidPushException();
            }
        }

        else if(card.getLevel()==2){
            if(slots.get(pos).size()==1 && slots.get(pos).get(0).getLevel()==1){
                slots.get(pos).push(card);
            }else {throw new InvalidPushException(); }
        }
        else if(card.getLevel()==3){
            if(slots.get(pos).size()==2 && slots.get(pos).get(1).getLevel()==2){
                slots.get(pos).push(card);
            }else {throw new InvalidPushException(); }
        }
        int nCards=0;
        for (Map.Entry<Integer, Stack<DevelopmentCard>> entry : slots.entrySet()) {
            nCards+=entry.getValue().size();
        }
        if(nCards==7){
            notifyObserver();
        }
    }

    /**
     * getter of the faithPath
     * @return the board's relative faithpath
     */
    public FaithPath getFaithPath() {
        return faithpath;
    }

    /**
     * method for adding a resource in strongbox
     * @param r is the resource
     * @throws IllegalResourceException if the resource is not accepted
     */
    public void addResourceInStrongbox(ResourceType r) throws IllegalResourceException {
        if(r==ResourceType.EMPTY || r==ResourceType.RED || r==ResourceType.UNKNOWN || r==ResourceType.WHITE)
            throw new IllegalResourceException();
        strongbox.replace(r,strongbox.get(r)+1);
    }
}
