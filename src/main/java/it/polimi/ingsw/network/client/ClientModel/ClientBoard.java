package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ClientBoard {
    private ClientFaithPath faithPath;
    private ClientDeposits deposits;
    private Map<Integer, Stack<DevelopmentCard>> slots=new HashMap<>();
    private Integer totalSlotPoints;
    private Integer totalCardsBought;
    //leaders

    public ClientBoard(){
        deposits=new ClientDeposits();
        faithPath=new ClientFaithPath();
        slots.put(1,new Stack<>());
        slots.put(2,new Stack<>());
        slots.put(3,new Stack<>());
        totalSlotPoints=0;
        totalCardsBought=0;
    }
    public String toString(){
        String stringBoard= faithPath.toString();
        stringBoard+=deposits.toString();
        stringBoard+=stringifySlots();
        return stringBoard;
    }
    public void push(Integer slot,DevelopmentCard d){
        if(slot!=null && d!=null && slots.get(slot)!=null){
            slots.get(slot).push(d);
            totalSlotPoints+=d.getPoints();
            totalCardsBought++;
        }
    }
    public void pop(Integer slot){
        if(slot!=null && slots.get(slot)!=null && slots.get(slot).size()>0){
            slots.get(slot).pop();
        }
    }
    public String recapSlots(){
        StringBuilder sb=new StringBuilder();
        for(int i=1;i<4;i++){
            sb.append(Color.ANSI_GREEN.escape()+"LEVEL "+i+": "+Color.RESET);
            for(int j=1;j<4;j++){
                for(DevelopmentCard d : slots.get(j)){
                    if(d.getLevel()==i)
                        sb.append(Utilities.modelColorToClientColor(d.getColor()).escape()+"■"+Color.RESET);
                }
            }
            sb.append(" | ");
        }
        sb.append("\n");
        return sb.toString();
    }
    public String stringifySlots(){
        StringBuilder sb=new StringBuilder();
        for(int i=1;i<4;i++){
            sb.append(Color.ANSI_PURPLE.escape()+"SLOT "+i+": "+Color.RESET);
            if(slots.get(i)!=null && slots.get(i).size()>0){
                sb.append(Utilities.stringify(slots.get(i).get(slots.get(i).size()-1)));
            }else sb.append("[ ]");
            sb.append("\n");
        }
        sb.append(Color.ANSI_PURPLE.escape()+"TOTAL SLOT POINTS: "+Color.ANSI_YELLOW.escape()+totalSlotPoints+Color.RESET+" | "+
                Color.ANSI_PURPLE.escape()+"TOTAL CARDS BOUGHT: "+Color.RESET+totalCardsBought+"\n");
        sb.append(Color.ANSI_PURPLE.escape()+"RECAP: "+Color.RESET+recapSlots());
        return sb.toString();
    }
    public ClientDeposits getDeposits() {
        return deposits;
    }

    public ClientFaithPath getFaithPath() {
        return faithPath;
    }
}
