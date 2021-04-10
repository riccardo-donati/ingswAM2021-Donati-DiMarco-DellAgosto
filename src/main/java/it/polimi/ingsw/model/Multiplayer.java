package it.polimi.ingsw.model;

import java.io.FileNotFoundException;
import java.util.List;

public class Multiplayer extends Game{
    public Multiplayer() {
        super();
    }
    /**
     * set the next player
     */
    @Override
    public void nextTurn(){
        //controllo se non ci sono risorse in pending/picked up????
        int index=getPlayers().indexOf(getCurrPlayer());
        setCurrPlayer(getPlayers().get((index+1)%(getPlayers().size()-1)));
    }

}
