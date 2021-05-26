package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class LobbyInfoMessage implements ClientMessage {
    private final List<String> nicknamesList;
    private final int num;

    public  LobbyInfoMessage(List<String> nicknames,int num){
        this.nicknamesList = nicknames;
        this.num=num;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String getMessage() {
        String message = "Lobby players: ("+nicknamesList.size()+"/"+num+")\n ";
        message += nicknamesList.toString().replace("[","").replace("]","");
        return message;
    }

    public List<String> getNickList(){
        return nicknamesList;
    }

    @Override
    public void accept(ClientVisitor visitor, Client client) {
        visitor.visit(this, client);
    }
}
