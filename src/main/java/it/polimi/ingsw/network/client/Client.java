package it.polimi.ingsw.network.client;

import com.google.gson.Gson;

import it.polimi.ingsw.network.Parser;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.messages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Client {
    private String hostName = "127.0.0.1"; //da sostituire con richiesta al player
    private PrintWriter out;
    private Scanner in;
    private BufferedReader stdIn;
    private Socket socket;
    private Gson gson;
    private ClientVisitorHandler clientHandlerVisitor = new ClientVisitorHandler();
    private ClientModel clientModel;

    private String currCommand="";

    public Client(){
        clientModel=new ClientModel();
    }
    public ClientModel getClientModel() {
        return clientModel;
    }


    public void handleStdIn(){
        String line="";
        while(!socket.isClosed()) {
            try {
                line = stdIn.readLine();
                Message m;
                try {
                    m= Parser.parse(currCommand+line,this);
                    out.println(gson.toJson(m,Message.class));
                } catch (IllegalCommandException e) {
                    System.out.println(Color.ANSI_RED.escape()+"Wrong syntax"+Color.RESET);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run(int serverPortNumber) {
        gson = Utilities.initializeGsonMessage();

        try {
            socket = new Socket(hostName, serverPortNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            new Thread(()->handleStdIn()).start();

        } catch (Exception e){
            System.out.println("Server not available");
            return;
        }
        String jsonString;
        while (!socket.isClosed()) {
            try {
                jsonString = in.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("Error, disconnecting . . .");
                in.close();
                out.close();
                break;
            }
            ClientMessage message=gson.fromJson(jsonString, ClientMessage.class);
            message.accept(clientHandlerVisitor,this);
        }
    }

    public Scanner getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getStdIn() {
        return stdIn;
    }

    public Gson getGson() {
        return gson;
    }

    public void setCurrCommand(String currCommand) {
        this.currCommand = currCommand;
    }

    public static void main(String[] args) throws IOException {
        Integer serverPortNumber = Utilities.loadServerPortNumber();
        new Client().run(serverPortNumber);
    }
}
