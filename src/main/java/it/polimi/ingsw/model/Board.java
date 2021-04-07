package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.Source;
import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.LeaderCard;


import java.util.*;

public class Board {
    private Map<ResourceType, Integer> strongbox = new HashMap<>();
    private Warehouse warehouse;
    private FaithPath faithpath;
    private Production baseProduction;
    private Map<Integer, Stack<DevelopmentCard>> slots;
    private List<Observer> observer = new ArrayList<>();

    public Board(Map<ResourceType, Integer> strongbox, Warehouse warehouse, FaithPath faithpath, Production baseProduction, Map<Integer, Stack<DevelopmentCard>> slots ) {
        strongbox.put(ResourceType.BLUE, 0);
        strongbox.put(ResourceType.GREY, 0);
        strongbox.put(ResourceType.VIOLET, 0);
        strongbox.put(ResourceType.YELLOW, 0);
        this.warehouse = warehouse;
        this.faithpath = faithpath;
        this.baseProduction = baseProduction;
        this.slots = slots;
    }

    /**
     * the function gives a map with a resource type and the relative number ad adds it to the player's strogbox
     * previously initialized to 0, it increments the relative key in the map
     * @param bought acquired resources from the market
     */
    public void depositInStrogbox(Map<ResourceType, Integer> bought ){
        for(ResourceType res : bought.keySet()) {                                   //per ogni risorsa nella mappa degli acquistati
            for (Map.Entry<ResourceType, Integer> entry : strongbox.entrySet()) {   //per ogni elemento nella mappa dei posseduti
                if (res.equals(entry.getKey())) {                                   //se il tipo corrisponde al tipo comprato
                    Integer curr = entry.getValue() + bought.get(res);              //curr prende il valore attuale della risorsa e somma il valore comprato
                    strongbox.put(res, curr);                                       //il nuovo valore del posseduto è curr
                }
            }
        }
    }

    /**
     * selects 2 resources from warehouse's maindepot using the relative funcion and stores in the strongbox a chosen resource
     * @param input 2 any kind of resources from the main depot in the warehouse
     * @param out one resource of any kind to store in the strongbox
     */
    public void setBaseProduction(ResourceType[] input ,ResourceType out ){}

    /**
     * add the observer in a list of observer
     * @param obs the observer that has to be added in the observer's list
     */
    public void addObserver(Observer obs){observer.add(obs);}

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
        score += faithpath.countFaithPoints();
        for (Map.Entry<Integer,Stack<DevelopmentCard>> entry : slots.entrySet()){
            for (DevelopmentCard dev : entry.getValue()){
                score += dev.getPoints();
            }
        }
        //anche dalle leader cards presenti in PlayerInGame
        return score;
    }
}
