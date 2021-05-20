package it.polimi.ingsw.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public interface Client {
    public Socket getSocket();
    public ClientModel getClientModel();
    public Gson getGson();
    public Scanner getIn();
    public PrintWriter getOut();

    // Updates:
    void visualizeSlotUpdate();
}
