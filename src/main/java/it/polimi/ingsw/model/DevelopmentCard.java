package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.enums.Color;

import java.util.List;

public class DevelopmentCard {
    @Expose
    private List<ResourceRequirement> cost;
    @Expose
    private Integer level;
    @Expose
    private Color color;
    @Expose
    private Production production;
    @Expose
    private Integer points;
    @Expose
    private String name;

    public DevelopmentCard(List<ResourceRequirement> cost, Integer level, Color color, Production production, Integer points) {
        this.cost = cost;
        this.level = level;
        this.color = color;
        this.production = production;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    /**
     * getter of the attribute color
     * @return the card's color
     */
    public Color getColor(){return color;}

    /**
     * getter of the attribute livello
     * @return the card's level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * getter of the card's cost
     * @return the list of resource requirements
     */
    public List<ResourceRequirement> getCost() {
        return cost;
    }

    /**
     * getter of the card's production
     * @return the card's production
     */
    public Production getProd() {
        return production;
    }

    /**
     * getter for the relative card's points
     * @return the card's points
     */
    public Integer getPoints(){ return points;}


}
