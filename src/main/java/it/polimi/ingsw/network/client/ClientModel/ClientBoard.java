package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.CLI.enums.Color;
import it.polimi.ingsw.network.client.CLI.enums.Resource;

import java.util.*;

public class ClientBoard {
    private final ClientFaithPath faithPath;
    private final ClientDeposits deposits;
    private final Map<Integer, Stack<DevelopmentCard>> slots = new HashMap<>();
    private Integer totalSlotPoints;
    private Integer totalCardsBought;
    private List<Resource> pendingResources = new ArrayList<>();
    private List<LeaderCard> leadersInHand = new ArrayList<>();
    private final List<LeaderCard> leadersInBoard = new ArrayList<>();
    private Map<Integer,String> discardedCards=new HashMap<>();
    private Map<Integer,String> playedCards=new HashMap<>();

    private List<Production> activeProductions = new ArrayList<>();
    private final Production baseProduction;
    private List<Production> extraProductions = new ArrayList<>();
    private List<ResourceDiscount> discounts = new ArrayList<>();


    public ClientBoard(){
        deposits = new ClientDeposits();
        faithPath = new ClientFaithPath();
        slots.put(1, new Stack<>());
        slots.put(2, new Stack<>());
        slots.put(3, new Stack<>());
        totalSlotPoints = 0;
        totalCardsBought = 0;
        baseProduction = new Production();
        try {
            baseProduction.addInput(ResourceType.UNKNOWN,2);
            baseProduction.addOutput(ResourceType.UNKNOWN,1);
        } catch (IllegalResourceException e) {
            e.printStackTrace();
        }

    }

    public ClientDeposits getDeposits() {
        return deposits;
    }

    public ClientFaithPath getFaithPath() {
        return faithPath;
    }

    public void putPlayedCard(Integer index,String ld){
        if(playedCards.get(index)!=null)
            playedCards.put(index+1,ld);
        else playedCards.put(index,ld);
    }

    public Map<Integer,String> getPlayedCards() { return playedCards; }

    public void putDiscardedCard(Integer index,String ld){
        if(discardedCards.get(index)!=null)
            discardedCards.put(index+1,ld);
        else discardedCards.put(index,ld);
    }

    public Map<Integer,String> getDiscardedCards() { return discardedCards; }

    public Production getBaseProduction() {
        return baseProduction;
    }

    public List<Production> getActiveProductions() {
        return activeProductions;
    }

    public List<LeaderCard> getLeadersInBoard() {
        return leadersInBoard;
    }

    public List<LeaderCard> getLeadersInHand() {
        return leadersInHand;
    }

    public List<Production> getExtraProductions() {
        return extraProductions;
    }

    public Map<Integer, Stack<DevelopmentCard>> getSlots() {
        return slots;
    }

    public Integer getTotalCardsBought() {
        return totalCardsBought;
    }

    public List<ResourceDiscount> getActiveDiscounts() {
        List<ResourceDiscount> list = new ArrayList<>();
        for (ResourceDiscount rd : discounts) {
            if (rd.isActivated()) list.add(rd);
        }
        return list;
    }

    public void setActiveProductions(List<Production> activeProductions) {
        this.activeProductions = activeProductions;
    }

    public void setLeadersInHand(List<LeaderCard> leadersInHand) {
        this.leadersInHand = leadersInHand;
    }

    public void resetProduction(){
        baseProduction.resetProduction();
        for(Production production : extraProductions) {
            production.resetProduction();
        }
    }

    public void addExtraProd (ResourceType resourceType) {
        Production production = new Production();
        try {
            production.addOutput(ResourceType.UNKNOWN,1);
            production.addInput(resourceType,1);
            production.addOutput(ResourceType.RED,1);
        } catch (IllegalResourceException e) {
            e.printStackTrace();
        }
        extraProductions.add(production);
    }

    public void toggleDiscount(ResourceType res) {
        for(ResourceDiscount discount : discounts){
            if(discount.getRes() == res) discount.toggle();
        }
    }

    public void addDiscount(ResourceType resourceType) {
        ResourceDiscount resourceDiscount = new ResourceDiscount(resourceType);
        discounts.add(resourceDiscount);
    }

    public String toString() {
        String stringBoard = faithPath.toString();
        stringBoard += deposits.toString();
        stringBoard += stringifySlots();
        stringBoard += stringifyLeaders();
        stringBoard += stringifyBaseProduction();
        return stringBoard;
    }

    public void push(Integer slot, DevelopmentCard card) {
        if (slot != null && card != null && slots.get(slot) != null) {
            slots.get(slot).push(card);
            totalSlotPoints += card.getPoints();
            totalCardsBought++;
        }
    }

    public DevelopmentCard pop(Integer slot) {
        if (slot != null && slots.get(slot) != null && slots.get(slot).size() > 0)
            return slots.get(slot).pop();
        return null;
    }

    public String stringifyActiveDiscounts() {
        StringBuilder sb=new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape() + "Active discounts: " + Color.RESET);
        for(ResourceDiscount rd: discounts){
            if(rd.isActivated()) sb.append("-").append(rd.getQuantity()).append(Utilities.resourceTypeToResource(rd.getRes()).label).append(" ");
        }
        return sb.toString();
    }

    public String stringifyProductions() {
        StringBuilder sb=new StringBuilder();
        sb.append(stringifyBaseProduction());
        sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
        sb.append(Color.ANSI_PURPLE.escape()).append("EXTRA PRODUCTION: "+Color.RESET);
        for(Production p : extraProductions){
            sb.append("[").append(Utilities.stringify(p)).append("]");
        }
        sb.append("\n");
        return sb.toString();
    }

    public String stringifyBaseProduction() {
        StringBuilder sb=new StringBuilder();
        sb.append("\n═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
        sb.append(Color.ANSI_PURPLE.escape()).append("BASE PRODUCTION: ").append(Color.RESET).append(Utilities.stringify(baseProduction));
        sb.append("\n");
        return sb.toString();
    }

    public String stringifyLeaders() {
        StringBuilder sb=new StringBuilder();
        sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
        sb.append(Color.ANSI_PURPLE.escape()+"LEADERS IN HAND: "+Color.RESET);
        for(LeaderCard ld : leadersInHand){
            sb.append(Utilities.stringify(ld));
            sb.append("\n                 ");
        }
        sb.append("\n");
        sb.append(Color.ANSI_PURPLE.escape()+"LEADERS IN BOARD: "+Color.RESET);
        for(LeaderCard ld : leadersInBoard){
            sb.append(Utilities.stringify(ld));
            sb.append("\n                  ");
        }
        sb.append("\n");
        return sb.toString();
    }

    public String recapSlots() {
        StringBuilder sb = new StringBuilder();
        for(int i=1;i<4;i++){
            sb.append(Color.ANSI_GREEN.escape()+"LEVEL "+i+": "+Color.RESET);
            for(int j=1;j<4;j++){
                for(DevelopmentCard d : slots.get(j)){
                    if(d.getLevel() == i)
                        sb.append(Utilities.modelColorToClientColor(d.getColor()).escape()+"■"+Color.RESET);
                }
            }
            sb.append(" | ");
        }
        sb.append("\n");
        return sb.toString();
    }

    public String stringifySlots(){
        StringBuilder sb = new StringBuilder();
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

    public String stringifyPending(){
        StringBuilder mex = new StringBuilder();
        if(pendingResources.size() > 0) {
            mex.append("Deposit these pending resources:\n[");
            for (Resource resource : pendingResources) {
                String res=resource.toString().toLowerCase();
                mex.append(res).append("(").append(resource.label).append("),");
                //mex.append(ResourceType.valueOfLabel(res.toString())+"("+Resource.valueOf()+")");
            }
            mex.deleteCharAt(mex.toString().length() - 1);
            mex.append("]");
        }
        return mex.toString();
    }

    public List<Resource> getPendingResources() {
        return pendingResources;
    }
}
