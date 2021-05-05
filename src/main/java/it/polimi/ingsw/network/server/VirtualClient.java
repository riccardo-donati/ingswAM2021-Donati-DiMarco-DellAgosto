package it.polimi.ingsw.network.server;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.network.messages.Message;

public class VirtualClient {
    @Expose
    private String nickname;
    private ClientHandler clientHandler;

    public VirtualClient(String nickname ,ClientHandler clientHandler){
        this.nickname=nickname;
        this.clientHandler=clientHandler;
    }

    public void send(Message m){
        clientHandler.getOut().println(clientHandler.getGson().toJson(m,Message.class));
        clientHandler.getOut().flush();
    }

    public String getNickname() {
        return nickname;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }
}
