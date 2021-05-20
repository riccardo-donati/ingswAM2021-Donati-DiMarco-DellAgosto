package it.polimi.ingsw.network.messages.updates;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.enums.TurnPhase;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientVisitor;

public class SlotUpdate implements Update {
    private Integer slot;
    private Integer row;
    private Integer col;

    public SlotUpdate(Integer slot,Integer row,Integer col){
        this.slot=slot;
        this.row=row;
        this.col=col;
    }

    @Override
    public void update(ClientModel clientModel) {
        DevelopmentCard dc=clientModel.getCardMatrix().popCard(row,col);
        if(dc!=null) clientModel.getCurrentBoard().push(slot,dc);

        clientModel.setTurnPhase(TurnPhase.ENDTURN);
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this,client);
    }
}
