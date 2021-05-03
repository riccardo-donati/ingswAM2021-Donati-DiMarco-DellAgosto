package it.polimi.ingsw.network.server;

import com.google.gson.annotations.Expose;

public class VirtualClient {
    @Expose
    private String nickname;
    private ClientHandler clientHandler;

    public VirtualClient(String nickname ,ClientHandler clientHandler){
        this.nickname=nickname;
        this.clientHandler=clientHandler;
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
