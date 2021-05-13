package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Utilities;
import it.polimi.ingsw.model.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
    private Game game;
    private Map<String,Integer> nickOrderMap=new HashMap<>();
    private List<List<LeaderCard>> lists;

    public List<String> getPlayerLeaderCardList(String nick){
        List<String> list=new ArrayList<>();
        for(LeaderCard ld : lists.get(nickOrderMap.get(nick)-1)){
            list.add(ld.getName());
        }
        return list;
    }
    public Map<String, Integer> getNickOrderMap() {
        return nickOrderMap;
    }

    public List<String> getOrderPlayerList() { return game.getListNickname(); }

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
     * @return  get the list of the 4 leaderCards for each player
     */
    public List<List<LeaderCard>> getLeaderCards(){
        try {
            lists=game.divideLeaderCards();
        } catch (EmptyPlayersException e) {
            System.out.println("No players in game");
        } catch (NullPointerException e){
            System.out.println("Game not initialized");
        }
        return lists;
    }
    public Integer getLorenzoUpdate(){
        return game.getLorenzoPosition();
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
