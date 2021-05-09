package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalLeaderCardsException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseLeadersCommand implements Command {
    private List<String> chosenLeaders;

    public List<String> getchosenLeaders() {
        return chosenLeaders;
    }


    public ChooseLeadersCommand(List<String> cl){
        this.chosenLeaders=cl;
    }
    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this,clientHandler);
    }


    public boolean doAction(Controller c,String nickname){
        if(!c.getPlayerLeaderCardList(nickname).containsAll(chosenLeaders)) return false;
        Game g=c.getGame();
        if(check() && g.getCurrentNickname().equals(nickname)) {
            Map<String, LeaderCard> map = g.getNameLeaderCardMap();
            List<LeaderCard> l = new ArrayList<>();
            l.add(map.get(chosenLeaders.get(0)));
            l.add(map.get(chosenLeaders.get(1)));
            for(LeaderCard lc : l){
                if(lc==null)return false;
            }
            try {
                g.chooseLeader(l);
            } catch (NonEmptyException | IllegalLeaderCardsException | IllegalActionException | IndexOutOfBoundsException | NullPointerException e) {
                return false;
            }
            return true;
        }else return false;
    }

    @Override
    public boolean check() {
        if(chosenLeaders.size()!=2) return false;
        for(String ld : chosenLeaders){
            if(ld==null || ld.equals("")) return false;
        }
        return true;
    }


}
