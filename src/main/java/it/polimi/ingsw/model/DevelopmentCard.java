package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ResourceType;

import java.util.ArrayList;
import java.util.List;

/**
 * generators of the attributes of the class
 */
public class DevelopmentCard {
    private List<ResourceRequirement> costo = new ArrayList<>();
    private Integer livello;
    private Color color;
    private Production production;
    private Integer points;

    /**
     * getter of the attribute Livello
     * @return the level of the relative card
     */
    public Integer getLivello() {
        return livello;
    }

    /**
     * getter of the card's cost
     * @return how much and what resource is needed to buy that card
     */
    public List<ResourceRequirement> getCosto() {
        return costo;
    }

    /**
     * getter of the card's production
     * @return what is needed to offer to activate and what will be got back
     */
    public Production getProd() {
        return production;
    }

    /**
     * getter for the relative card's points
     * @return the currant points earned by the bought card
     */
    public Integer getPoints(){ return points;}
}
