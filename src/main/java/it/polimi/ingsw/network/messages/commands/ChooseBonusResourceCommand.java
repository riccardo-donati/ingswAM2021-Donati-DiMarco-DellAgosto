package it.polimi.ingsw.network.messages.commands;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullSpaceException;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.UnknownNotFoundException;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.ServerVisitor;

public class ChooseBonusResourceCommand implements Command {
    private ResourceType res;
    private Integer id;

    public ChooseBonusResourceCommand(ResourceType res, Integer id){
        this.res=res;
        this.id=id;
    }

    public Integer getId() {
        return id;
    }

    public ResourceType getRes() {
        return res;
    }

    @Override
    public boolean doAction(Controller c, String nickname) {
        Game g=c.getGame();
        if(check() && g.getCurrentNickname().equals(nickname)){
            try {
                g.chooseResourceToDeposit(id,res);
            } catch (IllegalActionException | FullSpaceException | UnknownNotFoundException | IllegalResourceException | IndexOutOfBoundsException | NullPointerException e) {
                return false;
            }
            return true;
        }else return false;
    }

    @Override
    public boolean check() {
        if(res==null || id==null) return false;
        return id >= 1;
    }

    @Override
    public void accept(ServerVisitor visitor, ClientHandler clientHandler) {
        visitor.visit(this,clientHandler);
    }
}
