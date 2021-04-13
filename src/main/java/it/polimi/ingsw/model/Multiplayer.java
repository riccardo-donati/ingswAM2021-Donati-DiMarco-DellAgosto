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
    public void nextTurn(){
        //controllo se non ci sono risorse in pending/picked up????
        int index=getPlayers().indexOf(getCurrPlayer());
        setCurrPlayer(getPlayers().get((index+1)%(getPlayers().size())));
    }

    @Override
    public void addPlayer(String nick) throws FullGameException {
        if(getPlayers().size()==4) throw new FullGameException();
        super.addPlayer(nick);
    }

    @Override
    public Result endGame(){
        Result result=super.endGame();
        result.setWinner(result.checkWinner());
        return  result;
    }

}
