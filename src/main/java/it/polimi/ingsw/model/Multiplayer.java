package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;

import java.io.FileNotFoundException;
import java.util.List;

public class Multiplayer extends Game{
    private Integer nPlayers;

    protected Multiplayer(Integer n) throws IllegalPlayersNumberException {
        super();
        if(n<2 || n>4) throw new IllegalPlayersNumberException();
        this.nPlayers=n;
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
    public void startGame() throws IllegalResourceException, GameNotFullException {
        if(nPlayers!=getPlayers().size()) throw new GameNotFullException();
        super.startGame();
    }
    @Override
    public Result endGame(){
        Result result = super.endGame();
        result.setWinner(result.checkWinner());
        return result;
    }
    @Override
    public void addPlayer(String nick) throws FullGameException {
        if(getPlayers().size()==nPlayers) throw new FullGameException();
        super.addPlayer(nick);
    }
    //-----------------------------------------------------------------------------------------------------

}
