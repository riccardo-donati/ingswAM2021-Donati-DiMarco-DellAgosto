package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {
    private Map<String,Integer> results;
    private String winner;

    public Result(){
        this.results=new HashMap<>();

    }
    public String checkWinner(){
        int max=0;
        String win="";
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            if(entry.getValue()>max){
                win=entry.getKey();
                max=entry.getValue();
            }
        }
        return win;
    }
    public void setWinner(String winner){
        this.winner=winner;

    }
    public void addToResults(String nickname,Integer points){
        results.put(nickname,points);
    }
}
