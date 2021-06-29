package it.polimi.ingsw.network.server;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.network.messages.Message;

import java.util.Objects;

public class VirtualClient {
    @Expose
    private final String nickname;
    private ClientHandler clientHandler;

    public VirtualClient(String nickname ,ClientHandler clientHandler){
        this.nickname=nickname;
        this.clientHandler=clientHandler;
    }

    /**
     * send a message through the client handler's socket
     * @param m message sent
     */
    public void send(Message m){
        if(clientHandler!=null) {
            clientHandler.getOut().println(clientHandler.getGson().toJson(m, Message.class));
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualClient that = (VirtualClient) o;
        return Objects.equals(nickname, that.nickname) &&
                Objects.equals(clientHandler, that.clientHandler);
    }
}
