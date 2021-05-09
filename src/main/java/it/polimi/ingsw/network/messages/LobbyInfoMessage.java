package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.client.ClientVisitor;

import java.util.List;

public class LobbyInfoMessage implements ClientMessage {
    private List<String> nicknamesList;

    public  LobbyInfoMessage(List<String> nicknames){
        this.nicknamesList = nicknames;
    }

    @Override
    public String getMessage() {
        String message="Lobby players:\n ";
        message+=nicknamesList.toString().replace("[","").replace("]","");
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
