package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalLeaderCardsException;
import it.polimi.ingsw.model.exceptions.NonEmptyException;
import it.polimi.ingsw.model.exceptions.WaitingReconnectionsException;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.exceptions.NotYourTurnException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.ServerVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseLeadersCommand implements Command {
    private List<String> chosenLeaders;

    public List<String> getChosenLeaders() {
        return chosenLeaders;
    }


    public ChooseLeadersCommand(List<String> cl){
        this.chosenLeaders=cl;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this,clientHandler);
    }


    public void doAction(Controller c, String nickname) throws IllegalCommandException, NotYourTurnException, IllegalActionException, NonEmptyException, IllegalLeaderCardsException, WaitingReconnectionsException {
        if(check())c.chooseLeader(chosenLeaders,nickname);
        else throw new IllegalCommandException();
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
