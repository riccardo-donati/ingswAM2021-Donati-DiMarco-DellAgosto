package it.polimi.ingsw.network.server;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import it.polimi.ingsw.network.exceptions.ReconnectionException;
import it.polimi.ingsw.network.messages.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    @Expose
    private int port;
    private ServerSocket serverSocket;
    @Expose
    private List<VirtualClient> virtualClientList = new ArrayList<>();
    @Expose
    private List<Lobby> lobbies = new ArrayList<>();
    private Gson gson;
    private List<VirtualClient> waitingList = new ArrayList<>();
    private Map<Integer,String> clientHandlerNickMap = new HashMap<>();
    @Expose
    private Map<String,Integer> nickLobbyMap = new HashMap<>();

    public Server(int port) {
        gson = Utilities.initializeGsonMessage();
        this.port = port;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The server socket could not be created");
            System.out.println("Closing . . .");
            return;
        }
        System.out.println("Welcome to the Masters of Renaissance server!");

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("A new client connected");
                new Thread(new ClientHandler(socket, this)).start();
            } catch (IOException e) { // goes here if the server socket gets closed
                System.out.println("Closing . . .");
                saveServerStatus();
                break;
            }
        }
    }

    public void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveServerStatus(){
        Gson gsonSave = Utilities.initializeGsonLoadAndSave();

        String serverToJson = gsonSave.toJson(this, Server.class);
        FileWriter w;
        try {
            w = new FileWriter("src/main/resources/json/serverStatus.json");
            w.write(serverToJson);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The server state could not be saved");
        }
    }

    public static Server loadServerStatus(){
        Gson gsonLoad=Utilities.initializeGsonLoadAndSave();
        FileReader fr = null;
        try {
            fr = new FileReader("src/json/serverStatus.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader b;
        b=new BufferedReader(fr);
        String json="";
        try {
            json=b.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Server s=gsonLoad.fromJson(json,Server.class);

        for(Lobby l : s.lobbies){
            l.getPlayers().clear();
            l.setGson(Utilities.initializeGsonMessage());
        }
        for (Map.Entry<String, Integer> entry : s.getNickLobbyMap().entrySet()) {
            if(entry.getValue()>Lobby.getN()) Lobby.setN(entry.getValue());
            VirtualClient vc=s.searchVirtualClient(entry.getKey());
            Lobby l= s.searchLobby(entry.getValue());
            l.addPlayer(vc);
        }
        s.clientHandlerNickMap=new HashMap<>();
        s.waitingList=new ArrayList<>();
        s.gson=Utilities.initializeGsonMessage();
        return s;
    }

    public void handleDisconnection(Integer chId){
        if(clientHandlerNickMap.get(chId)!=null){
            String nick=clientHandlerNickMap.get(chId);
            Lobby l=searchLobby(nickLobbyMap.get(nick));
            l.notifyLobby(new GenericMessage(nick +  " disconnected!"));
            if(!l.isStarted()){
                try {
                    searchVirtualClient(nick).getClientHandler().closeConnection();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }


        }
    }

    public synchronized int checkLobby(){
        for(Lobby l : lobbies){
            if(!l.isPiena())
                return l.getIdLobby();
        }
        return -1;
    }

    public Lobby searchLobby(int id){
        for(Lobby l : lobbies){
            if(l.getIdLobby()==id){
                return l;
            }
        }
        return null;
    }

    public void removeVirtualClient(Integer chId){
        if(clientHandlerNickMap.get(chId)!=null) {
            VirtualClient vcToRemove = searchVirtualClient(clientHandlerNickMap.get(chId));
            virtualClientList.remove(vcToRemove);
            clientHandlerNickMap.remove(chId);
            if(nickLobbyMap.get(vcToRemove.getNickname())!=null) {
                int lobbyId=nickLobbyMap.remove(vcToRemove.getNickname());
                Lobby l=searchLobby(lobbyId);
                l.removePlayer(vcToRemove);
                //l.notifyLobby(new GenericMessage(vcToRemove.getNickname()+" disconnected!"));
                if(l.getPlayers().size()==0){
                    lobbies.remove(l);
                }else {
                    l.notifyLobby(new LobbyInfoMessage(l.getNames()));
                }
            }
        }
    }

    public VirtualClient searchVirtualClient(String nick){
        for(VirtualClient vc : virtualClientList){
            if(vc.getNickname().equals(nick))
                return vc;
        }
        return null;
    }

    public VirtualClient searchVirtualClient(Integer idCH){
        for(VirtualClient vc : virtualClientList){
            if(vc.getClientHandler().getId()==idCH)
                return vc;
        }
        return null;
    }

    public void reconnect(VirtualClient vc,VirtualClient oldVc){
        clientHandlerNickMap.remove(oldVc.getClientHandler().getId());
        oldVc.setClientHandler(vc.getClientHandler());
        clientHandlerNickMap.put(oldVc.getClientHandler().getId(),oldVc.getNickname());
    }

    public synchronized void addVirtualClient(VirtualClient vc) throws ReconnectionException {
        VirtualClient vLook=searchVirtualClient(vc.getNickname());
        if(vLook!=null && vLook.getClientHandler()==null){
            //caso riconnessione dopo disconnessione del server
            vLook.setClientHandler(vc.getClientHandler());
            clientHandlerNickMap.put(vc.getClientHandler().getId(),vc.getNickname());
            int idLobby=nickLobbyMap.get(vLook.getNickname());
            Lobby l=searchLobby(idLobby);
            l.notifyLobby(new GenericMessage(vLook.getNickname()+" reconnected after server disconnection!"));
            throw new ReconnectionException();
        }
        if(vLook!=null && vLook.getClientHandler().isConnesso()){
            //caso nickname già preso
            vc.getClientHandler().getOut().println(gson.toJson(new GenericMessage("Nickname already taken"),Message.class));
            vc.getClientHandler().getOut().println(gson.toJson(new RegisterRequest(),Message.class));
            vc.getClientHandler().getOut().flush();
            throw new IllegalArgumentException();
        }else if(vLook!=null && !vLook.getClientHandler().isConnesso()){
            //caso riconnessione dopo disconnessione del client
            reconnect(vc,vLook);
            int idLobby=nickLobbyMap.get(vLook.getNickname());
            Lobby l=searchLobby(idLobby);
            l.notifyLobby(new GenericMessage(vLook.getNickname()+" reconnected!"));
            throw new ReconnectionException();
        }else {
            //caso nickname nuovo
            waitingList.add(vc);
            clientHandlerNickMap.put(vc.getClientHandler().getId(), vc.getNickname());
            for (VirtualClient v : virtualClientList) {
                if(v.getClientHandler()!=null) {
                    Message m = new GenericMessage(vc.getNickname() + " si è connesso!");
                    v.getClientHandler().getOut().println(gson.toJson(m, Message.class));
                    v.getClientHandler().getOut().flush();
                }
            }
            if (checkLobby() != -1) {
                for (Lobby l : lobbies) {
                    if (l.getIdLobby() == checkLobby()) {
                        l.addPlayer(vc);
                        waitingList.remove(vc);
                        virtualClientList.add(vc);
                        nickLobbyMap.put(vc.getNickname(), l.getIdLobby());
                        l.notifyLobby(new LobbyInfoMessage(l.getNames()));
                        if (l.isPiena()) {
                            l.startGame();
                            //saveServerStatus();
                        }
                    }
                }
            }
        }
    }

    public Map<Integer, String> getClientHandlerNickMap() {
        return clientHandlerNickMap;
    }

    public Map<String, Integer> getNickLobbyMap() {
        return nickLobbyMap;
    }

    public List<VirtualClient> getVirtualClientList() {
        return virtualClientList;
    }

    public synchronized void createNewLobby(int nPlayers, ClientHandler ch){
        for(VirtualClient vc: waitingList){
            if(vc.getClientHandler().equals(ch)){
                Lobby newLobby=new Lobby(nPlayers,vc);
                lobbies.add(newLobby);
                virtualClientList.add(vc);
                waitingList.remove(vc);
                nickLobbyMap.put(vc.getNickname(),newLobby.getIdLobby());
                Message r=new GenericMessage("Lobby creata");
                vc.getClientHandler().getOut().println(gson.toJson(r,Message.class));
                vc.getClientHandler().getOut().flush();
                if(newLobby.isPiena()){
                    newLobby.startGame();
                    //saveServerStatus();
                }
                return;
            }
        }
    }

        private static Integer loadPortNumber() throws FileNotFoundException, NullPointerException {
        JsonReader reader = new JsonReader(new FileReader("src/main/resources/json/serverSettings.json"));
        Map<String, Integer> map = new Gson().fromJson(reader, new TypeToken<Map<String, Integer>>() {}.getType());
        return map.get("portNumber");
    }

    public static void main(String[] args) {
        Integer port = 0;
        try {
            port = loadPortNumber();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("serverSettings.json file not found");
            System.out.println("Closing . . .");
            return;
        }

        if (port == null) {
            System.out.println("Port number value not found");
            System.out.println("Closing . . .");
            return;
        } else if (port < 1000 || port > 10000) {
            System.out.println("The port number value has to be between 1000 and 9999");
            System.out.println("Closing . . .");
            return;
        } else System.out.println("Port number value loaded successfully");

        Server server = new Server(port);
        server.startServer();
    }
}
