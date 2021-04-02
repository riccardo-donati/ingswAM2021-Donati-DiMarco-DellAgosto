package it.polimi.ingsw.model;
import it.polimi.ingsw.model.enums.PopeFavorState;
import it.polimi.ingsw.model.interfaces.BoardObserver;

import java.lang.Object;
import java.util.*;

public class FaithPath {
    private Integer position;
    private Map<Integer, Integer> pointsPosition = new HashMap<>();
    private List<PopeFavor> popeFavorList = new ArrayList<>();
    private List<BoardObserver> observer = new ArrayList<>();

    /**
     * initialize the position, the Points Position as an HAshMap and the 3 PopeFavor cards with their values
     */
    public FaithPath() {
        this.position = 0;
        pointsPosition.put(3,1);
        pointsPosition.put(6,2);
        pointsPosition.put(9,4);
        pointsPosition.put(12,6);
        pointsPosition.put(15,9);
        pointsPosition.put(18,12);
        pointsPosition.put(21,16);
        pointsPosition.put(24,20);
        popeFavorList.add(new PopeFavor(PopeFavorState.UNACTIVE, 2, 5,8));
        popeFavorList.add(new PopeFavor(PopeFavorState.UNACTIVE, 3, 13,16));
        popeFavorList.add(new PopeFavor(PopeFavorState.UNACTIVE, 4, 19,24));
    }

    /**
     * moves the current player position for a certain value
     * @param move express how much the player position has to move forward
     */
    public void addToPosition(Integer move) {
        position += move;
    }

    /**
     * Counts the current player's score by adding every value of the Map from the beginning to the player position
     * @return the player's score
     */
    public Integer countFaithPoints(){
        Integer curr = 0;
        for (Map.Entry<Integer,Integer> entry : pointsPosition.entrySet()){
            if(position >= entry.getKey())
                curr += entry.getValue();
        }

        for (PopeFavor pf : popeFavorList){
            if (pf.getState() == PopeFavorState.ACTIVE)
                curr += pf.getPoints();
        }
        
        return curr;
    }

    /**
     * getter for the attribute
     * @return the current position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     *
     */
    public void notifyObserver(){ }

    /**
     *
     * @param obs observer to the FaithPath
     */
    public void addObserver(BoardObserver obs){
        observer.add(obs);
    }

}
