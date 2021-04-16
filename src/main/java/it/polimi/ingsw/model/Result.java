package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {
    private Map<String, Integer[]> results;
    private List<String> winner;

    /**
     * constructor
     */
    protected Result(){
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
    protected void setWinner(List<String> winner){
        this.winner.addAll(winner);

        //used for debugging
        for (String s : results.keySet()) {
            System.out.println("Player: " + s + "\nScore: " + results.get(s)[0] + "\nResources: " + results.get(s)[1] +  "\n");
        }
        for (String s : this.winner)
            System.out.println("Winner(s): " + s + "\nScore: " + results.get(s)[0] + "\nResources: " + results.get(s)[1] +  "\n");
    }

    /**
     * add a player to the Result object
     * @param nickname player's name
     * @param points player's point
     */
    protected void addToResults(String nickname, Integer points, Integer resources){
        results.put(nickname, new Integer[]{points, resources});
    }
}
