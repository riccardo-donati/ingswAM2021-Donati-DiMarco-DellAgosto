package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullGameException;

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
    protected void nextTurn(){
        int index=getPlayers().indexOf(getCurrPlayer());
        setCurrPlayer(getPlayers().get((index+1)%(getPlayers().size())));
    }

    @Override
    protected void addPlayer(String nick) throws FullGameException {
        if(getPlayers().size()==4) throw new FullGameException();
        super.addPlayer(nick);
    }
    //----------------PublicInterface----------------------------------------------------------------------
    @Override
    protected Result endGame(){
        Result result=super.endGame();
        result.setWinner(result.checkWinner());
        return  result;
    }
    //-----------------------------------------------------------------------------------------------------

}
