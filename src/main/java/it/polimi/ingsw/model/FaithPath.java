package it.polimi.ingsw.model;
import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.enums.PopeFavorState;
import it.polimi.ingsw.model.interfaces.BoardObserver;

import java.util.*;

public class FaithPath {
    @Expose
    private Integer position;
    @Expose
    private Map<Integer, Integer> pointsPosition;
    @Expose
    private List<PopeFavor> popeFavorList;
    private List<BoardObserver> observer = new ArrayList<>();

    public List<BoardObserver> getObserver() {
        return observer;
    }

    /**
     * initialize the position, the Points Position as an HAshMap and the 3 PopeFavor cards with their values
     */
    protected FaithPath() {
        this.position = 0;
        pointsPosition = new HashMap<>();
        pointsPosition.put(3,1);
        pointsPosition.put(6,2);
        pointsPosition.put(9,4);
        pointsPosition.put(12,6);
        pointsPosition.put(15,9);
        pointsPosition.put(18,12);
        pointsPosition.put(21,16);
        pointsPosition.put(24,20);
        popeFavorList = new ArrayList<>();
        popeFavorList.add(new PopeFavor(PopeFavorState.UNACTIVE, 2, 5,8));
        popeFavorList.add(new PopeFavor(PopeFavorState.UNACTIVE, 3, 12,16));
        popeFavorList.add(new PopeFavor(PopeFavorState.UNACTIVE, 4, 19,24));
    }

    /**
     * moves the current player position for a certain value
     * @param move express how much the player position has to move forward
     */
    protected void addToPosition(Integer move) {
        if(position + move > 24) {
            position = 24;
        }
        else position += move;
        for (PopeFavor pf : popeFavorList) {
            if(position >= pf.getPopeSpace() && pf.getState().equals(PopeFavorState.UNACTIVE))
                notifyPopeFavor();
        }
        if(position == 24)
            notifyEndGame();
    }


    /**
     * Counts the current player's score by adding every value of the Map from the beginning to the player position
     * @return the player's score
     */
    protected Integer countFaithPoints(){
        Integer curr = 0, max = 0;

        for (Map.Entry<Integer,Integer> entry : pointsPosition.entrySet()){
            if(position >= entry.getKey())
                curr = entry.getValue();
                if(curr > max)
                    max = curr;
        }

        for (PopeFavor pf : popeFavorList){
            if (pf.getState() == PopeFavorState.ACTIVE)
                max += pf.getPoints();
        }
        
        return max;
    }

    /**
     * getter for the attribute
     * @return the current position
     */
    protected Integer getPosition() {
        return position;
    }

    /**
     * getter of the Pope Favor list
     * @return the list of all the pope favor cards
     */
    protected List<PopeFavor> getPopeFavorList() {
        return popeFavorList;
    }

    /**
     *
     */
    protected void notifyEndGame(){
        for(BoardObserver obs : observer){
            obs.updateEndGame();
        }
    }

    protected void notifyPopeFavor(){
        for(BoardObserver obs : observer){
            obs.updatePopeFavor();
        }
    }

    /**
     *
     * @param obs observer to the FaithPath
     */
    protected void addObserver(BoardObserver obs){
        observer.add(obs);
    }


}
