package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Utilities;
import it.polimi.ingsw.model.exceptions.FullGameException;
import it.polimi.ingsw.model.exceptions.GameNotFullException;
import it.polimi.ingsw.model.exceptions.IllegalPlayersNumberException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
    private Game game;
    private Map<String,Integer> nickOrderMap=new HashMap<>();
    public Map<String, Integer> getNickOrderMap() {
        return nickOrderMap;
    }

    /**
     * @return the nickname of the current player
     */
    public String getCurrentPlayer(){
        try {
            return game.getCurrentNickname();
        }catch (NullPointerException e){
            System.out.println("Game not initialized or started");
        }
        return "";
    }
    /**
     * create the instance of Game
     * @param numberOfPlayers is the number of the lobby's players
     */
    public void initializeGame(int numberOfPlayers){
        if(game!=null){
            System.out.println("Game already initialized!");
            return;
        }
        try {
            game=Game.createGame(numberOfPlayers);
        } catch (IllegalPlayersNumberException e) {
            System.out.println("Game not initialized because numberOfPlayers is an illegal value!");
        }
    }

    /**
     * add the players to the Game instance
     * @param nicknames is a list of nicknames
     */
    public void addPlayers(List<String> nicknames){
        if(nicknames!=null){
            for(String nick : nicknames){
                try {
                    game.addPlayer(nick);
                } catch (FullGameException e) {
                    System.out.println("The game is full, not all nicknames added");
                } catch (IllegalArgumentException e){
                    System.out.println(e.getMessage());
                } catch (NullPointerException e){
                    System.out.println("Game not initialized");
                    return;
                }
            }
        }
    }

    /**
     * start the game and get a map with the order of the players
     */
    public void start(){
        try {
            game.startGame();
        } catch (IllegalResourceException e) {
            System.out.println("Internal error, please reinitialize the game");
        } catch (GameNotFullException e) {
            System.out.println("Can't start the game because it's not full! Add more players");
        } catch (NullPointerException e){
            System.out.println("Game not initialized");
            return;
        }
        nickOrderMap=game.getNickOrderMap();
    }
    public Game getGame() {
        return game;
    }
}
