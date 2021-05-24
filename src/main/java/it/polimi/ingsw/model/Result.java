package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {
    @Expose
    private Map<String, Integer[]> results;
    @Expose
    private List<String> winner;

    /**
     * constructor
     */
    public Result(){
        this.results = new HashMap<>();
        this.winner = new ArrayList<>();
    }

    public Map<String, Integer[]> getResults() {
        return results;
    }

    public List<String> getWinner() {
        return winner;
    }

    /**
     * Check the provisional winner on the points basis
     * @return the nickname of the provisional winner
     */
    protected List<String> checkWinner(){
        int maxPoints = 0;
        int maxResources = 0;
        List<String> winners = new ArrayList<>();

        for (String s : results.keySet()) {
            if(results.get(s)[0] > maxPoints) {
                winners.clear();
                winners.add(s);
                maxPoints = results.get(s)[0];
                maxResources = results.get(s)[1];
            } else if (results.get(s)[0] == maxPoints) {
                if (results.get(s)[1] > maxResources) {
                    winners.clear();
                    winners.add(s);
                    maxResources = results.get(s)[1];
                } else if (results.get(s)[1] == maxResources)
                    winners.add(s);
            }
        }
        return winners;
    }

    /**
     * Set the winner of the game
     * @param winner is the nickname of the winner
     */
    public void setWinner(List<String> winner){
        this.winner.addAll(winner);

        //used for debugging
        /*
        for (String s : results.keySet()) {
            System.out.println("Player: " + s + "\nScore: " + results.get(s)[0] + "\nResources: " + results.get(s)[1] +  "\n");
        }
        for (String s : this.winner){
            if(!s.equals("Lorenzo"))
                System.out.println("Winner(s): " + s + "\nScore: " + results.get(s)[0] + "\nResources: " + results.get(s)[1] +  "\n");
            else  System.out.println("Lorenzo wins you LOSE!");

        }

         */
    }

    /**
     * add a player to the Result object
     * @param nickname player's name
     * @param points player's point
     */
    public void addToResults(String nickname, Integer points, Integer resources){
        results.put(nickname, new Integer[]{points, resources});
    }
}
