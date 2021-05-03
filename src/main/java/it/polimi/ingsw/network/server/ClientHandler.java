package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.ingsw.model.InterfaceAdapter;
import it.polimi.ingsw.network.messages.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private int id;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private Server server;
    private Gson gson;
    private Thread pinger;
    private Thread timer;
    private boolean connesso;
    private boolean ping;
    private boolean timeout;
    private ServerVisitorHandler serverHandlerVisitor;

    private static int cont=0;

    public int getId() {
        return id;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void closeConnection() throws InterruptedException {
        Message m=new DisconnectionMessage();
        out.println(gson.toJson(m,Message.class));
        out.flush();
        server.removeVirtualClient(id);
        Thread.sleep(3000);
        out.close();
        in.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("User disconnected with id: "+id);

    }

    public boolean isConnesso() {
        return connesso;
    }

    public void setConnesso(boolean connesso) {
        this.connesso = connesso;
    }

    public Thread getPinger() {
        return pinger;
    }

    public void stopTimer(){
        if(timer!=null){
            timer.interrupt();
            timer=null;
        }
    }
    public void startTimer(int ms){
        timer=new Thread(()->{
            try {
                Thread.sleep(ms);
                timeout=true;

            } catch (InterruptedException e) {  }
        });
        timer.start();
    }
    public Scanner getIn() { return in; }

    public PrintWriter getOut() { return out; }

    public void send(Message m){
        out.println(gson.toJson(m,Message.class));
        out.flush();
    }

    public ClientHandler(Socket socket, Server server) {
        this.serverHandlerVisitor=new ServerVisitorHandler();
        this.socket = socket;
        this.server=server;
        this.connesso=true;
        cont++;
        this.id=cont;
        this.timeout=false;
        this.pinger = new Thread(() -> {
            ping=true;
            while(ping) {
                try {
                    ping=false;
                    send(new PingRequest());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("Player sconnesso");
            server.handleDisconnection(id);
            connesso=false;
        });
    }


    public void run() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new InterfaceAdapter<ServerMessage>());
        builder.registerTypeAdapter(ClientMessage.class, new InterfaceAdapter<ClientMessage>());
        builder.registerTypeAdapter(Message.class, new InterfaceAdapter<Message>());
        gson = builder.create();
        try {

            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());

            Message req=new RegisterRequest();
            out.println(gson.toJson(req,Message.class));
            out.flush();
            startTimer(50000);
            //
            String line="";
            while (line!="quit") {
                try {
                    line = in.nextLine();
                }catch (NoSuchElementException e){
                    System.out.println("disconnecting Client Handler number "+id+" . . .");
                    return;
                }
                Message mex=gson.fromJson(line,Message.class);
                handleMessage(mex);
            }
            // Chiudo gli stream e il socket
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean isTimeout() {
        return timeout;
    }

    public Server getServer() {
        return server;
    }

    public Gson getGson() {
        return gson;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }

    public void handleMessage(Message m) {
        ServerMessage r = (ServerMessage) m;
        r.accept(serverHandlerVisitor, this);
    }
}