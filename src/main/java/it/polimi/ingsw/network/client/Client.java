package it.polimi.ingsw.network.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.ingsw.model.InterfaceAdapter;
import it.polimi.ingsw.network.messages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    String hostName = "127.0.0.1";
    int portNumber = 1234;
    PrintWriter out;
    Scanner in;
    BufferedReader stdIn;
    Socket echoSocket;
    Gson gson;
    ClientVisitorHandler clientHandlerVisitor=new ClientVisitorHandler();
    public void run() throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new InterfaceAdapter<ServerMessage>());
        builder.registerTypeAdapter(ClientMessage.class, new InterfaceAdapter<ClientMessage>());
        builder.registerTypeAdapter(Message.class, new InterfaceAdapter<Message>());
        gson = builder.create();
        try {
            echoSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new Scanner(echoSocket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        }catch (Exception e){
            System.out.println("Server not available");
            return;
        }
        String l= null;
        Message mex;
        while(!echoSocket.isClosed()){
            try {
                l = in.nextLine();
            }catch (NoSuchElementException e){
                System.out.println("Error mi disconnetto  . . .");
                in.close();
                out.close();
                break;
            }
            mex=gson.fromJson(l,Message.class);
            try { handleMessage(mex);
            } catch (IOException e) {
                System.out.println("Error mi disconnetto. . .");
                in.close();
                out.close();
                break;
            }
        }
    }

    public Scanner getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public Socket getEchoSocket() {
        return echoSocket;
    }

    public BufferedReader getStdIn() {
        return stdIn;
    }

    public Gson getGson() {
        return gson;
    }

    public void handleMessage(Message m) throws IOException {
        ClientMessage cm=(ClientMessage)m;
        cm.accept(clientHandlerVisitor,this);
        /*if(m instanceof DisconnectionMessage){
            System.out.println(m.getMessage());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            in.close();
            out.close();
            echoSocket.close();
            return;
        }
        if(m instanceof GenericMessage) {
            System.out.println(m.getMessage());
        }
        if(m instanceof RegisterRequest) {
            RegisterRequest rr = (RegisterRequest) m;
            System.out.println(rr.getMessage());
            String userInput = stdIn.readLine();
            Message a = new RegisterResponse(userInput);
            out.println(gson.toJson(a, Message.class));

        }
        if(m instanceof PlayerNumberRequest){
            System.out.println(m.getMessage());
            int n=Integer.parseInt(stdIn.readLine());
            Message m2=new PlayerNumberResponse(n);
            out.println(gson.toJson(m2,Message.class));
        }
        if(m instanceof LobbyInfoMessage){
            LobbyInfoMessage li=(LobbyInfoMessage)m;
            for(String n : li.getNickList()){
                System.out.println("LobbyPlayer: "+n);
            }
        }
        if(m instanceof PingRequest){
            Message pr=new PingResponse();
            //System.out.println(m.getMessage());
            out.println(gson.toJson(pr,Message.class));
            out.flush();
        }

         */
    }
    public static void main(String[] args) throws IOException {
        new Client().run();
    }

}
