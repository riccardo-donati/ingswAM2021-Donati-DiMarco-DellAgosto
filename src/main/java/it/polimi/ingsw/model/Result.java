package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {
    private Map<String,Integer> results;
    private String winner;

    /**
     * constructor
     */
    protected Result(){
        this.results=new HashMap<>();
    }

    /**
     * Check the provisional winner on the points basis
     * @return the nickname of the provisional winner
     */
    protected String checkWinner(){
        int max=0;
        String win="";
        for (String s : results.keySet()) {
            if(results.get(s) > max){
                win = s;
                max = results.get(s);
            }
        }
        return win;
    }

    /**
     * Set the winner of the game
     * @param winner is the nickname of the winner
     */
    protected void setWinner(String winner){
        this.winner=winner;
        //used for debugging
        for(String s : results.keySet()){
            System.out.println("Player: "+ s + "\nScore: "+ results.get(s)+"\n");
        }
        System.out.println("The winner is: " + winner + "\nWith a score of: "+ results.get(winner));
    }

    /**
     * add a player to the Result object
     * @param nickname player's name
     * @param points player's point
     */
    protected void addToResults(String nickname,Integer points){
        results.put(nickname,points);
    }
}
