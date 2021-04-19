package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullGameException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;

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


    //----------------PublicInterface----------------------------------------------------------------------
    @Override
    public Result endGame(){
        Result result = super.endGame();
        result.setWinner(result.checkWinner());
        return result;
    }
    @Override
    public void addPlayer(String nick) throws FullGameException, IllegalResourceException {
        if(getPlayers().size()==4) throw new FullGameException();
        super.addPlayer(nick);
    }
    //-----------------------------------------------------------------------------------------------------

}
