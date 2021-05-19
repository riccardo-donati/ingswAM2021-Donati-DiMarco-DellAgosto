package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.model.exceptions.*;

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
    public void startGame() throws IllegalResourceException, GameNotFullException, GameNotFullException {
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

    @Override
    public void passTurn() throws IllegalActionException {
        if(!getCurrPlayer().isActive()){ //disconnected player case
            if(getGamePhase()==GamePhase.SETUP && getCurrPlayer().getOrder()==getPlayers().size()){
                setGamePhase(GamePhase.ONGOING);
                setTurnPhase(TurnPhase.STARTTURN);
            }
            else if(getCurrPlayer().getOrder()==getPlayers().size() && isEndGameTrigger()){
                setGamePhase(GamePhase.ENDGAME);
                Result result=endGame();
                notifyEndGameResult(result);
            }
            nextTurn();
            return;
        }
        super.passTurn();
    }
}
