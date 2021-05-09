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
    private List<String> choosenLeaders=new ArrayList<>();

    public List<String> getChoosenLeaders() {
        return choosenLeaders;
    }


    public ChooseLeadersCommand(List<String> cl){
        this.choosenLeaders=cl;
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
        if(!c.getPlayerLeaderCardList(nickname).containsAll(choosenLeaders)) return false;
        Game g=c.getGame();
        if(check() && g.getCurrentNickname().equals(nickname)) {
            Map<String, LeaderCard> map = g.getNameLeaderCardMap();
            List<LeaderCard> l = new ArrayList<>();
            l.add(map.get(choosenLeaders.get(0)));
            l.add(map.get(choosenLeaders.get(1)));
            for(LeaderCard lc : l){
                if(lc==null)return false;
            }
            try {
                g.chooseLeader(l);
            } catch (NonEmptyException e) {
                return false;
            } catch (IllegalLeaderCardsException e) {
                return false;
            } catch (IllegalActionException e) {
                return false;
            }
            return true;
        }else return false;
    }

    @Override
    public boolean check() {
        if(choosenLeaders.size()!=2) return false;
        for(String ld : choosenLeaders){
            if(ld==null || ld.equals("")) return false;
        }
        return true;
    }


}
