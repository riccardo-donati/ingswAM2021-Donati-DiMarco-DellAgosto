package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.InvalidPushException;
import it.polimi.ingsw.model.interfaces.BoardObserver;

import java.util.*;

public class Board {
    private Map<ResourceType, Integer> strongbox;
    private Warehouse warehouse;
    private FaithPath faithpath;
    private Production baseProduction;
    private Map<Integer, Stack<DevelopmentCard>> slots;
    private List<BoardObserver> observers = new ArrayList<>();

    /**
     * Constructor of the Board class: initializes the map for the strongbox, creates a new warehouse and a new faith path
     * sets the base production to have two UNKNOWN in input and one UNKNOWN in output
     * creates the map of stacks for the development cards
     */
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
     * getter of the strongbox
     * @return the strongbox
     */
    protected Map<ResourceType, Integer> getStrongBox(){
        return strongbox;
    }

    /**
     * getter of the warehouse
     * @return the warehouse
     */
    protected Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * getter of the  faith path
     * @return the faith path
     */
    protected FaithPath getFaithPath() {
        return faithpath;
    }

    /**
     * getter of the base production
     * @return the base production
     */
    protected Production getBaseProduction() {
        return baseProduction;
    }

    /**
     * getter of the cards slots map
     * @return the cards slots map
     */
    protected Map<Integer, Stack<DevelopmentCard>> getSlots() {
        return slots;
    }

    /**
     * counts all the points of a player's board, from cards to faith path
     * @return the current player score given by the sum of all the board's points
     */
    protected Integer countBoardsPoints(){
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

    public List<BoardObserver> getObservers() {
        return observers;
    }

    /**
     * add a single resource in strongbox
     * @param r is the resource
     * @throws IllegalResourceException if the resource is not accepted
     */
    protected void addResourceInStrongbox(ResourceType r) throws IllegalResourceException {
        if(r==ResourceType.EMPTY || r==ResourceType.RED || r==ResourceType.UNKNOWN || r==ResourceType.WHITE)
            throw new IllegalResourceException();
        strongbox.replace(r,strongbox.get(r)+1);
    }

    public Integer countNresInStrongbox(){
        return getStrongBox().values().stream().reduce(0,(a,b)->a+b);
    }
    /**
     * add a map of resources to the strongbox
     * @param prod acquired resources from the production
     */
    protected void depositInStrongbox(Map<ResourceType, Integer> prod){
        for(ResourceType res : prod.keySet()) {
            if (strongbox.containsKey(res))
                strongbox.replace(res, strongbox.get(res) + prod.get(res));
            else strongbox.put(res, prod.get(res)); //most likely useless
        }
    }

    /**
     * removes one resource of the specified resource type
     * @param r resource type to be removed
     * @return the resource type passed as parameter
     */
    protected ResourceType removeResourceFromStrongbox(ResourceType r){
        if(strongbox.containsKey(r) && strongbox.get(r)>0){
            strongbox.replace(r, strongbox.get(r)-1);
            return r;
        }else return ResourceType.EMPTY;
    }

    /**
     * pushes a card into a specified position
     * @param pos the position where the card should be added
     * @param card the development card that has to be added
     */
    protected void pushDCard (Integer pos, DevelopmentCard card) throws InvalidPushException {
        if(card.getLevel()==1){
            if(slots.get(pos).size()==0){
                slots.get(pos).push(card);
            }else throw new InvalidPushException();
        } else if(card.getLevel()==2){
            if(slots.get(pos).size()==1 && slots.get(pos).get(0).getLevel()==1){
                slots.get(pos).push(card);
            }else throw new InvalidPushException();
        } else if(card.getLevel()==3){
            if(slots.get(pos).size()==2 && slots.get(pos).get(1).getLevel()==2){
                slots.get(pos).push(card);
            }else throw new InvalidPushException();
        } else throw new InvalidPushException();

        int nCards=0;
        for (Map.Entry<Integer, Stack<DevelopmentCard>> entry : slots.entrySet())
            nCards+=entry.getValue().size();
        if (nCards==7) notifyObserver();
    }

    /**
     * add the observer passed as parameter to the list of observers
     * @param obs the observer that has to be added in the observers list
     */
    protected void addObserver(BoardObserver obs) {
        observers.add(obs);
    }

    /**
     * notify the observer
     */
    protected void notifyObserver(){
        for(BoardObserver obs : observers){
            obs.updateEndGame();
        }
    }

    protected  Integer countStrongboxResources() {
        return strongbox.values().stream().reduce(0, Integer::sum);
    }

    protected Integer countTotalResources() {
        return warehouse.countWarehouseResource() + countStrongboxResources();
    }
}