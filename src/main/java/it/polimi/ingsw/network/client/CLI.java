package it.polimi.ingsw.network.client;

import com.google.gson.Gson;

import it.polimi.ingsw.network.Parser;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.messages.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class CLI implements Client{
    private final String serverIP;
    private final Integer serverPortNumber;
    private Socket socket;

    private PrintWriter out;
    private Scanner in;
    private BufferedReader stdIn;

    private Gson gson;

    private final ClientVisitorHandler clientVisitorHandler;
    private final ClientModel clientModel;
    private String currCommand = "";

    public CLI(String serverIP, Integer serverPortNumber){
        this.serverIP = serverIP;
        this.serverPortNumber = serverPortNumber;
        clientModel = new ClientModel();
        clientVisitorHandler = new ClientVisitorHandler();
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }



    public Scanner getIn() {
        return in;
    }

    public Gson getGson() {
        return gson;
    }

    public ClientModel getClientModel() {
        return clientModel;
    }

    public void setCurrCommand(String currCommand) {
        this.currCommand = currCommand;
    }

    public void handleStdIn(){
        String line;
        while (!socket.isClosed()) {
            try {
                line = stdIn.readLine();
                Message message;
                try {
                    message = Parser.parse(currCommand + line, this);
                    if (message != null)
                        out.println(gson.toJson(message, Message.class));
                } catch (IllegalCommandException e) {
                    System.out.println(Color.ANSI_RED.escape() + "Wrong syntax" + Color.RESET);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        gson = Utilities.initializeGsonMessage();

        try {
            socket = new Socket(serverIP, serverPortNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            new Thread(this::handleStdIn).start();
        } catch (Exception e){
            System.out.println("Server not available");
            return;
        }
        System.out.println("Connected to server port " + serverPortNumber + " and IP address " + serverIP + "\n");
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
            ClientMessage message = gson.fromJson(jsonString, ClientMessage.class);
            message.accept(clientVisitorHandler, this);
        }
    }

    public static void main(String[] args) throws IOException {
        String serverIP;
        Integer serverPortNumber;
        System.out.println("Loading connection settings from file . . .");

        try {
            serverIP = Utilities.loadServerIP();
            serverPortNumber = Utilities.loadServerPortNumber();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("serverSettings.json file not found");
            System.err.println("Closing . . .");
            return;
        }

        new CLI(serverIP, serverPortNumber).run();
    }

    @Override
    public void visualizeSlotUpdate() {
        if(getClientModel().getCurrentNickname().equals(getClientModel().getNickname())){
            System.out.println(getClientModel().getCurrentBoard().stringifySlots());
        }
    }
}
