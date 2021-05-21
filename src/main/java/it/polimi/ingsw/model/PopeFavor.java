package it.polimi.ingsw.model;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.enums.PopeFavorState;

public class PopeFavor {
    @Expose
    private PopeFavorState state;
    @Expose
    private Integer points;
    @Expose
    private Integer initialPos;
    @Expose
    private Integer finalPos;

    protected PopeFavor(PopeFavorState state, Integer points, Integer initialPos, Integer finalPos) {
        this.state = state;
        this.points = points;
        this.initialPos = initialPos;
        this.finalPos = finalPos;
    }

    /**
     * changes the state of a specific Pope Favor cards, setting it ACTIVE, UNACTIVE or DISCARDED
     * @param state is how will be setted
     */
    protected void changeState(PopeFavorState state) { this.state = state; }

    /**
     * getter of the attributes
     * @return the current state of the Pope Favor
     */
    protected PopeFavorState getState() { return state; }

    /**
     * getter of the attributes
     * @return the value of the current Pope Favor
     */
    protected Integer getPoints() {
        return points;
    }

    /**
     * checks if the player current position is inside the Pope Favor
     * @param position is where the Player is when the function is called
     * @return true if the current position is inside a small area in the Pope Favor
     */
    protected boolean checkInside(Integer position){
        return position >= initialPos;
    }

    /**
     * getter of the last position of the relative Pope Favor
     * @return the last postion of the PopeFavor
     */
    protected Integer getPopeSpace() { return finalPos; }
}
