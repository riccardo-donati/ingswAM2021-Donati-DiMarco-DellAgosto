package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.Source;
import it.polimi.ingsw.model.interfaces.BoardObserver;

import java.util.*;

public class Board {
    private Map<ResourceType, Integer> strongbox;
    private Warehouse warehouse;
    private FaithPath faithpath;
    private Production baseProduction;
    private Map<Integer, Stack<DevelopmentCard>> slots;
    private List<BoardObserver> observer = new ArrayList<>();

    public Board() {
        strongbox = new HashMap<>();
        strongbox.put(ResourceType.BLUE, 0);
        strongbox.put(ResourceType.GREY, 0);
        strongbox.put(ResourceType.VIOLET, 0);
        strongbox.put(ResourceType.YELLOW, 0);
        this.warehouse = new Warehouse();
        this.faithpath = new FaithPath();
        this.baseProduction = new Production();
        slots = new HashMap<>();
        slots.put(1, new Stack<>());
        slots.put(2, new Stack<>());
        slots.put(3, new Stack<>());
    }

    /**
     * the function gives a map with a resource type and the relative number ad adds it to the player's strongbox
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

    /**
     * selects 2 resources from warehouse's maindepot using the relative function and stores in the strongbox a chosen resource
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
    public void addObserver(BoardObserver obs){observer.add(obs);}

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
    public void notifyObserver(){}

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
        score += warehouse.countWarehousePoints() + faithpath.countFaithPoints();
        return score;       //i punti dati dalle leader cards li somma direttamente la funzione chiamante
    }

    /**
     * pushes a card into a specified position
     * @param pos the position where the cards should be added
     * @param card the development card that has to be added
     */
    public void pushDCard (Integer pos, DevelopmentCard card){
        slots.get(pos).push(card);                                  //levelexception
        //size = 7 end game
    }

    /**
     * getter of the faithPath
     * @return the board's relative faithpath
     */
    public FaithPath getFaithPath() {
        return faithpath;
    }

    public Map<Integer, Stack<DevelopmentCard>> getSlots() {
        return slots;
    }

}
