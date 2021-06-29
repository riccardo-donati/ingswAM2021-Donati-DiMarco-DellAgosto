package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.interfaces.*;
import it.polimi.ingsw.network.client.CLI.enums.Color;
import it.polimi.ingsw.network.client.CLI.enums.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.server.Controller;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualClient;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public abstract class Utilities {

    /**
     *load the server port number from json file
     * @return the port
     * @throws NullPointerException if reader is null
     */
    public static Integer loadServerPortNumber() throws NullPointerException, FileNotFoundException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("json/serverSettings.json");
        if(is!=null) {
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            Map<String, String> map = new Gson().fromJson(reader, new TypeToken<Map<String, String>>() {
            }.getType());
            return Integer.parseInt(map.get("portNumber"));
        }
        return null;
    }

    /**
     * load the IP from json file
     * @return the IP on form of String
     * @throws NullPointerException if the readers is null
     */
    public static String loadServerIP() throws NullPointerException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("json/serverSettings.json");
        if(is!=null) {
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            Map<String, String> map = new Gson().fromJson(reader, new TypeToken<Map<String, String>>() {
            }.getType());
            return map.get("ip");
        }
        return null;
    }

    /**
     * save the server in a json file
     * @param server reference of the server
     * @param gson reference of the gson
     */
    public static void saveServerStatus(Server server, Gson gson){
        String serverToJson=gson.toJson(server,Server.class);
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter("serverStatus.json");
            fileWriter.write(serverToJson);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the gson for messages
     * @return the instance of the created gson
     */
    public static Gson initializeGsonMessage(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new InterfaceAdapter<ServerMessage>());
        builder.registerTypeAdapter(ClientMessage.class, new InterfaceAdapter<ClientMessage>());
        builder.registerTypeAdapter(Message.class, new InterfaceAdapter<Message>());
        builder.registerTypeAdapter(Token.class, new InterfaceAdapter<Message>());
        return builder.create();
    }

    /**
     * initialize the gson for loading and saving
     * @return the instance of the created gson
     */
    public static Gson initializeGsonLoadAndSave(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(PublicInterface.class, new InterfaceAdapter<PublicInterface>());
        builder.registerTypeAdapter(Requirement.class, new InterfaceAdapter<Requirement>());
        builder.registerTypeAdapter(SpecialAbility.class, new InterfaceAdapter<SpecialAbility>());
        builder.registerTypeAdapter(Token.class, new InterfaceAdapter<Token>());
        builder.registerTypeAdapter(Marble.class, new InterfaceAdapter<Marble>());
        builder.registerTypeAdapter(BoardObserver.class, new InterfaceAdapter<BoardObserver>());
        builder.registerTypeAdapter(Game.class, new InterfaceAdapter<Game>());
        builder.enableComplexMapKeySerialization();
        return builder.excludeFieldsWithoutExposeAnnotation().create();
        //return builder.create();
    }

    /**
     * transform the Result in readable String
     * @param result is the Result of the Game
     * @return the result in String
     */
    public static String stringify(Result result){
        StringBuilder sb=new StringBuilder();
        for (String s : result.getResults().keySet()) {
            sb.append("Player: ").append(s).append("\nScore: ").append(result.getResults().get(s)[0]).append("\nResources: ").append(result.getResults().get(s)[1]).append("\n").append("\n");
        }
        for (String s : result.getWinner()){
            if(!s.equals("Lorenzo"))
                sb.append("Winner(s): ").append(s).append("\nScore: ").append(result.getResults().get(s)[0]).append("\nResources: ").append(result.getResults().get(s)[1]).append("\n").append("\n");
            else  sb.append("Lorenzo wins you LOSE!").append("\n");

        }
        return sb.toString();
    }

    /**
     * transform the Token in readable String
     * @param t is the Token
     * @return the token in String
     */
    public static String stringify(Token t){
        if(t==null) return "";
        StringBuilder sb=new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape()).append("LORENZO TURN: "+Color.RESET);
        if(t instanceof TokenDiscard){
            sb.append("Discarded ").append(((TokenDiscard) t).getQuantity()).append(" ").append(Utilities.modelColorToClientColor(((TokenDiscard) t).getColor()).escape()).append("■").append(Color.RESET).append(" cards\n");
        }
        else if(t instanceof TokenPush){
            sb.append("Black cross +").append(((TokenPush) t).getQuantity());
        }
        else if(t instanceof TokenPushShuffle){
            sb.append("Black cross +").append(((TokenPushShuffle) t).getQuantity()).append(" and tokens shuffle");
        }
        return sb.toString();
    }

    /**
     * load the leader cards from the json file
     * @return the List of leader cards
     */
    public static List<LeaderCard> loadLeaderCardsFromJSON() {
        List<LeaderCard> list;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Requirement.class, new InterfaceAdapter<Requirement>());
        builder.registerTypeAdapter(SpecialAbility.class, new InterfaceAdapter<SpecialAbility>());
        Gson gson = builder.create();

        Type foundListType=new TypeToken<ArrayList<LeaderCard>>(){}.getType();
        JsonReader reader;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("json/leaderCard.json");
        if (is != null) {
            reader = new JsonReader(new InputStreamReader(is));
            list=gson.fromJson(reader,foundListType);
            return list;
        }
        return null;
    }

    /**
     * load the development cards from the json file
     * @return the List of development cards
     */
    public static List<DevelopmentCard> loadDevelopmentCardsFromJSON() {
        Gson gson=new Gson();
        Type foundListType=new TypeToken<ArrayList<DevelopmentCard>>(){}.getType();
        JsonReader reader;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("json/developmentCard.json");
        if (is != null) {
            reader = new JsonReader(new InputStreamReader(is));
            return gson.fromJson(reader,foundListType);
        }
        return null;

    }

    /**
     * Convert a ResourceType in a Resource
     * @param res is the ResourceType
     * @return the converted Resource
     */
    public static Resource resourceTypeToResource(ResourceType res){
        return Resource.valueOf(res.label.toUpperCase());
    }

    /**
     * Convert a Color to a ClientColor
     * @param color is the Model color
     * @return the Client converted color
     */
    public static Color modelColorToClientColor(it.polimi.ingsw.model.enums.Color color){
        if(color== it.polimi.ingsw.model.enums.Color.BLUE) return Color.ANSI_BLUE;
        if(color== it.polimi.ingsw.model.enums.Color.VIOLET) return Color.ANSI_PURPLE;
        if(color== it.polimi.ingsw.model.enums.Color.GREEN) return Color.ANSI_GREEN;
        if(color== it.polimi.ingsw.model.enums.Color.YELLOW) return Color.ANSI_YELLOW;

        return Color.ANSI_BLUE; //default
    }

    /**
     * transform the DevelopmentCard in readable String
     * @param d is the DevelopmentCard
     * @return the DevelopmentCard in String
     */
    public static String stringify(DevelopmentCard d) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        sb.append("COLOR: ").append(Utilities.modelColorToClientColor(d.getColor()).escape()).append("■").append(Color.RESET).append(" | ");
        sb.append("LEVEL: ").append(d.getLevel()).append(" | ");
        sb.append("COST: ");
        List<ResourceRequirement> rrList = d.getCost();
        for (ResourceRequirement rr : rrList) {
            sb.append(rr.getQuantity()).append(Utilities.resourceTypeToResource(rr.getResource()).label).append(" ");
        }
        sb.append("| ");
        sb.append( "PRODUCTION: ");
        sb.append(Utilities.stringify(d.getProd()));
        sb.append("| POINTS: ").append(Color.ANSI_YELLOW.escape()).append(d.getPoints()).append(Color.RESET);
        sb.append(" ]");

        return sb.toString();
    }

    /**
     * transform the Production in readable String
     * @param p is the Production
     * @return the production in String
     */
    public static String stringify(Production p){
        StringBuilder sb=new StringBuilder();
        sb.append("(");
        for (Map.Entry<ResourceType, Integer> entry : p.getInput().entrySet()) {
            sb.append(entry.getValue()).append(Utilities.resourceTypeToResource(entry.getKey()).label);
        }
        sb.append(")").append(Color.ANSI_RED.escape()).append("⇒").append(Color.RESET).append("(");
        for (Map.Entry<ResourceType, Integer> entry : p.getOutput().entrySet()) {
            sb.append(entry.getValue()).append(Utilities.resourceTypeToResource(entry.getKey()).label);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * transform the LeaderCard in readable String
     * @param ld is the LeaderCard
     * @return the LeaderCard in String
     */
    public static String stringify(LeaderCard ld){
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        List<Requirement> rList=ld.getRequirements();
        sb.append(Color.ANSI_GREEN.escape()).append("REQUIREMENTS: ").append(Color.RESET);
        for(Requirement req : rList){
            if(req instanceof ResourceRequirement){
                ResourceRequirement rr=(ResourceRequirement)req;
                sb.append(rr.getQuantity()).append(Utilities.resourceTypeToResource(rr.getResource()).label);
                sb.append(" ");
            }
            if(req instanceof CardRequirement){
                CardRequirement cr=(CardRequirement)req;
                sb.append(cr.getQuantity()).append(Utilities.modelColorToClientColor(cr.getColor()).escape()).append("■").append(Color.RESET);
                sb.append(" ");
            }
            if(req instanceof LevelCardRequirement){
                LevelCardRequirement lcr=(LevelCardRequirement)req;
                sb.append(lcr.getQuantity()).append("(").append(Utilities.modelColorToClientColor(lcr.getColor()).escape()).append("■").append(Color.RESET).append(" lv").append(lcr.getLevel()).append(")");
                sb.append(" ");
            }
        }
        sb.append("| ").append(Color.ANSI_GREEN.escape()).append("SPECIAL ABILITIES: ").append(Color.RESET);
        List<SpecialAbility> sList=ld.getSpecialAbilities();
        for(SpecialAbility spec : sList){
            if(spec instanceof Discount){
                Discount d=(Discount)spec;
                sb.append("-1 ").append(Utilities.resourceTypeToResource(d.getResourceType()).label);
                sb.append(" ");
            }
            if(spec instanceof WhiteTo){
                WhiteTo wt=(WhiteTo) spec;
                sb.append("● ⇒ ").append(Utilities.resourceTypeToResource(wt.getResourceType()).label);
                sb.append(" ");
            }
            if(spec instanceof ExtraDeposit){
                ExtraDeposit ed=(ExtraDeposit) spec;
                sb.append("[ ][ ] of ").append(Utilities.resourceTypeToResource(ed.getResourceType()).label);
                sb.append(" ");
            }
            if(spec instanceof ExtraProduction){
                ExtraProduction ep=(ExtraProduction) spec;
                sb.append(Utilities.resourceTypeToResource(ep.getResourceType()).label).append(" ⇒ (").append(Resource.FAITH.label).append(" + ").append(Resource.QUESTIONMARK.label).append(")");
                sb.append(" ");
            }
        }
        sb.append("| ").append(Color.ANSI_GREEN.escape()).append("POINTS: ").append(Color.ANSI_YELLOW.escape()).append(ld.getPoints()).append(Color.RESET);
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * Initialize the Matrix of Development Card with the passed list
     * @param list is the source of development cards
     * @return the reference of the matrix
     */
    public static Stack<DevelopmentCard>[][] initializeCardMatrix(List<DevelopmentCard> list){
        Stack<DevelopmentCard>[][] cardMatrix = new Stack[3][4];
        for (int r = 0; r < 3; r++){
            for (int c = 0; c < 4; c++){
                cardMatrix[r][c] = new Stack<>();
            }
        }
        List<DevelopmentCard> copy=new ArrayList<>(list);
        Random rn=new Random();
        for(int i=0;i<list.size();i++){
            int n=rn.nextInt(copy.size());
            DevelopmentCard dc=copy.get(n);
            cardMatrix[dc.getLevel()-1][dc.getColor().ordinal()].push(dc);
            copy.remove(dc);
        }
        return cardMatrix;
    }

    /**
     * convert a deposit in a client deposit
     * @param d is the deposit
     * @param me is a char, 'm'-> main, 'e'->extra deposit
     * @return the client deposit
     */
    public static ClientDeposit depositToClientDeposit(Deposit d,char me){
        List<Resource> listRes=new ArrayList<>();
        for(int i=0;i<d.getSpace().length;i++){
            if(d.getSpace()[i]!=ResourceType.EMPTY){
                listRes.add(Utilities.resourceTypeToResource(d.getSpace()[i]));
            }
        }
        if(me=='m') return new ClientDeposit(d.getId(),listRes);
        else if(me=='e')return new ClientDeposit(d.getId(),listRes);
        else return null;
    }

    /**
     * convert a Warehouse in a List of Client Deposits
     * @param wh is the Warehouse
     * @return the related List of Client Deposit
     */
    public static List<ClientDeposit> warehouseToListOfClientDeposits(Warehouse wh){
        List<ClientDeposit> list=new ArrayList<>();
        for(Deposit d : wh.getMaindepot()){
            ClientDeposit cd=Utilities.depositToClientDeposit(d,'m');
            if(cd!=null)list.add(cd);
        }
        for(Deposit d: wh.getExtradepots()){
            ClientDeposit cd=Utilities.depositToClientDeposit(d,'e');
            if(cd!=null)list.add(cd);
        }
        return list;
    }

    /**
     * Convert a Strongbox in a Client Strongbox
     * @param s is the strongbox
     * @return the Client Strongbox (Map of Resource,Integer)
     */
    public static Map<Resource,Integer> strongboxToClientStrongbox(Map<ResourceType,Integer> s){
        Map<Resource,Integer> strongbox=new HashMap<>();
        for (Map.Entry<ResourceType, Integer> entry : s.entrySet()) {
            strongbox.put(Utilities.resourceTypeToResource(entry.getKey()),entry.getValue());
        }
        return strongbox;
    }

    /**
     * the title of the game in ASCII ART
     * @return the string
     */
    public static String MORTitle() {
        return "--------------------------------------------------------------\n" +
                " ╔╦╗╔═╗╔═╗╔╦╗╔═╗╦═╗╔═╗ ╔═╗╔═╗ ╦═╗╔═╗╔╗╔╔═╗╦╔═╗╔═╗╔═╗╔╗╔╔═╗╔═╗\n" +
                " ║║║╠═╣╚═╗ ║ ║╣ ╠╦╝╚═╗ ║ ║║╣  ╠╦╝║╣ ║║║╠═╣║╚═╗╚═╗╠═╣║║║║  ║╣ \n" +
                " ╩ ╩╩ ╩╚═╝ ╩ ╚═╝╩╚═╚═╝ ╚═╝╩   ╩╚═╚═╝╝╚╝╩ ╩╩╚═╝╚═╝╩ ╩╝╚╝╚═╝╚═╝ \n" +
                "--------------------------------------------------------------\n";
    }

    /**
     * load the Server from the json
     * @return the loaded Server
     */
    public static Server loadServerStatus(){
        Gson gsonLoad=Utilities.initializeGsonLoadAndSave();
        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader("serverStatus.json"));
        } catch (FileNotFoundException e) {
            System.out.println("Previous server status not found, creating a new server . . .");
            return new Server(1337);
        }
        Server s=gsonLoad.fromJson(reader,Server.class);
        List<Integer> idsToRemove=new ArrayList<>();
        for(Controller l : s.getLobbies()){
            if(l.getGameState()== GamePhase.NOTSTARTED){
                Integer idLobby=l.getIdLobby();
                idsToRemove.add(idLobby);
            }else {
                l.getPlayersInLobby().clear();
                l.setGson(Utilities.initializeGsonMessage());
                l.setDisconnected(true);
            }
        }
        //remove the not started lobbies
        for(Integer id : idsToRemove){
            Controller lobby=s.searchLobby(id);
            s.removeLobby(lobby);
            for(VirtualClient vc : lobby.getPlayersInLobby()){
                s.unregisterClient(vc);
            }
        }
        for (Map.Entry<String, Integer> entry : s.getNickLobbyMap().entrySet()) {
            if(entry.getValue()>= Controller.getGlobalID()) Controller.setGlobalID(entry.getValue()+1);
            VirtualClient vc=s.searchVirtualClient(entry.getKey());
            Controller l= s.searchLobby(entry.getValue());
            l.addPlayerInLobby(vc);
        }
        s.setClientHandlerNickMap(new HashMap<>());
        s.setWaitingList(new ArrayList<>());

        //add the observers
        for(Controller c : s.getLobbies()){
            c.setServer(s);
            c.setGameObservers();
            c.disconnectAllPlayers();
            c.resetCurrentPlayerRef();
        }
        return s;
    }
}
