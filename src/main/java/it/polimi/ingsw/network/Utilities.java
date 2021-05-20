package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.interfaces.Requirement;
import it.polimi.ingsw.model.interfaces.Token;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.server.Server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Utilities {

    public static Integer loadServerPortNumber() throws FileNotFoundException, NullPointerException {
        JsonReader reader = new JsonReader(new FileReader("src/main/resources/json/serverSettings.json"));
        Map<String, String> map = new Gson().fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
        return Integer.parseInt(map.get("portNumber"));
    }

    public static String loadServerIP() throws FileNotFoundException, NullPointerException {
        JsonReader reader = new JsonReader(new FileReader("src/main/resources/json/serverSettings.json"));
        Map<String, String> map = new Gson().fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
        return map.get("ip");
    }

    public static void saveServerStatus(Server server, Gson gson){
        String serverToJson=gson.toJson(server,Server.class);
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter("src/main/resources/json/serverStatus.json");
            fileWriter.write(serverToJson);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Gson initializeGsonMessage(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new InterfaceAdapter<ServerMessage>());
        builder.registerTypeAdapter(ClientMessage.class, new InterfaceAdapter<ClientMessage>());
        builder.registerTypeAdapter(Message.class, new InterfaceAdapter<Message>());
        //we send the token on lorenzo update
        builder.registerTypeAdapter(Token.class, new InterfaceAdapter<Message>());
        return builder.create();
    }

    public static Gson initializeGsonLoadAndSave(){
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        return builder.excludeFieldsWithoutExposeAnnotation().create();
    }
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
    public static String stringify(Token t){
        if(t==null) return "";
        StringBuilder sb=new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape()).append("LORENZO TURN: "+Color.RESET);
        if(t instanceof TokenDiscard){
            sb.append("Discarded ").append(((TokenDiscard) t).getQuantity()).append(" ").append(Utilities.modelColorToClientColor(((TokenDiscard) t).getColor()).escape()+"■"+Color.RESET).append(" cards\n");
        }
        else if(t instanceof TokenPush){
            sb.append("Black cross +").append(((TokenPush) t).getQuantity());
        }
        else if(t instanceof TokenPushShuffle){
            sb.append("Black cross +").append(((TokenPushShuffle) t).getQuantity()).append(" and tokens shuffle");
        }
        return sb.toString();
    }

    public static List<LeaderCard> loadLeaderCardsFromJSON() {
        List<LeaderCard> list;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Requirement.class, new InterfaceAdapter<Requirement>());
        builder.registerTypeAdapter(SpecialAbility.class, new InterfaceAdapter<SpecialAbility>());
        Gson gson = builder.create();

        Type foundListType=new TypeToken<ArrayList<LeaderCard>>(){}.getType();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/resources/json/leaderCard.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("leaderCard.json not found");
        }
        list=gson.fromJson(reader,foundListType);
        return list;
    }

    public static List<DevelopmentCard> loadDevelopmentCardsFromJSON() {
        Gson gson=new Gson();
        Type foundListType=new TypeToken<ArrayList<DevelopmentCard>>(){}.getType();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("src/main/resources/json/developmentCard.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("DevelopmentCard.json not found");
        }
        return gson.fromJson(reader,foundListType);
    }

    public static Resource resourceTypeToResource(ResourceType res){
        return Resource.valueOf(res.label.toUpperCase());
    }

    public static Color modelColorToClientColor(it.polimi.ingsw.model.enums.Color color){
        if(color== it.polimi.ingsw.model.enums.Color.BLUE) return Color.ANSI_BLUE;
        if(color== it.polimi.ingsw.model.enums.Color.VIOLET) return Color.ANSI_PURPLE;
        if(color== it.polimi.ingsw.model.enums.Color.GREEN) return Color.ANSI_GREEN;
        if(color== it.polimi.ingsw.model.enums.Color.YELLOW) return Color.ANSI_YELLOW;

        return Color.ANSI_BLUE; //default
    }

    public static String stringify(DevelopmentCard d) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        sb.append("COLOR: " + Utilities.modelColorToClientColor(d.getColor()).escape() + "■" + Color.RESET+" | ");
        sb.append("LEVEL: "+ d.getLevel() + " | ");
        sb.append("COST: ");
        List<ResourceRequirement> rrList = d.getCost();
        for (ResourceRequirement rr : rrList) {
            sb.append(rr.getQuantity() + Utilities.resourceTypeToResource(rr.getResource()).label + " ");
        }
        sb.append("| ");
        sb.append( "PRODUCTION: ");
        /*Map<ResourceType, Integer> input = d.getProd().getInput();
        Map<ResourceType, Integer> output = d.getProd().getOutput();
        for (Map.Entry<ResourceType, Integer> entry : input.entrySet()) {
            sb.append(entry.getValue() + Utilities.resourceTypeToResource(entry.getKey()).label + " ");
        }
        sb.append(Color.ANSI_RED.escape() + "⇒ " + Color.RESET);
        for (Map.Entry<ResourceType, Integer> entry : output.entrySet()) {
            sb.append(entry.getValue() + Utilities.resourceTypeToResource(entry.getKey()).label + " ");
        }*/
        sb.append(Utilities.stringify(d.getProd()));
        sb.append("| POINTS: " + Color.ANSI_YELLOW.escape() + d.getPoints() + Color.RESET);
        sb.append(" ]");

        return sb.toString();
    }
    public static String stringify(Production p){
        StringBuilder sb=new StringBuilder();
        sb.append("(");
        for (Map.Entry<ResourceType, Integer> entry : p.getInput().entrySet()) {
            sb.append(entry.getValue()).append(Utilities.resourceTypeToResource(entry.getKey()).label);
        }
        sb.append(")").append(Color.ANSI_RED.escape()+"⇒"+ Color.RESET+"(");
        for (Map.Entry<ResourceType, Integer> entry : p.getOutput().entrySet()) {
            sb.append(entry.getValue()).append(Utilities.resourceTypeToResource(entry.getKey()).label);
        }
        sb.append(")");
        return sb.toString();
    }
    public static String stringify(LeaderCard ld){
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        List<Requirement> rList=ld.getRequirements();
        sb.append(Color.ANSI_GREEN.escape()+"REQUIREMENTS: "+Color.RESET);
        for(Requirement req : rList){
            if(req instanceof ResourceRequirement){
                ResourceRequirement rr=(ResourceRequirement)req;
                sb.append(rr.getQuantity()+Utilities.resourceTypeToResource(rr.getResource()).label);
                sb.append(" ");
            }
            if(req instanceof CardRequirement){
                CardRequirement cr=(CardRequirement)req;
                sb.append(cr.getQuantity()+Utilities.modelColorToClientColor(cr.getColor()).escape()+"■"+Color.RESET);
                sb.append(" ");
            }
            if(req instanceof LevelCardRequirement){
                LevelCardRequirement lcr=(LevelCardRequirement)req;
                sb.append(lcr.getQuantity()+"("+Utilities.modelColorToClientColor(lcr.getColor()).escape()+"■"+Color.RESET+" lv"+lcr.getLevel()+")");
                sb.append(" ");
            }
        }
        sb.append("| "+Color.ANSI_GREEN.escape()+"SPECIAL ABILITIES: "+Color.RESET);
        List<SpecialAbility> sList=ld.getSpecialAbilities();
        for(SpecialAbility spec : sList){
            if(spec instanceof Discount){
                Discount d=(Discount)spec;
                sb.append("-1 "+Utilities.resourceTypeToResource(d.getResourceType()).label);
                sb.append(" ");
            }
            if(spec instanceof WhiteTo){
                WhiteTo wt=(WhiteTo) spec;
                sb.append("● ⇒ "+Utilities.resourceTypeToResource(wt.getResourceType()).label);
                sb.append(" ");
            }
            if(spec instanceof ExtraDeposit){
                ExtraDeposit ed=(ExtraDeposit) spec;
                sb.append("[ ][ ] of "+Utilities.resourceTypeToResource(ed.getResourceType()).label);
                sb.append(" ");
            }
            if(spec instanceof ExtraProduction){
                ExtraProduction ep=(ExtraProduction) spec;
                sb.append(Utilities.resourceTypeToResource(ep.getResourceType()).label+" ⇒ ("+Resource.FAITH.label+" + "+Resource.QUESTIONMARK.label+")");
                sb.append(" ");
            }
        }
        sb.append("| "+Color.ANSI_GREEN.escape()+"POINTS: "+Color.ANSI_YELLOW.escape()+ ld.getPoints()+Color.RESET);
        sb.append(" ]");
        return sb.toString();
    }
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
     * @return
     */
    public static ClientDeposit depositToClientDeposit(Deposit d,char me){
        List<Resource> listRes=new ArrayList<>();
        for(int i=0;i<d.getSpace().length;i++){
            if(d.getSpace()[i]!=ResourceType.EMPTY){
                listRes.add(Utilities.resourceTypeToResource(d.getSpace()[i]));
            }
        }
        if(me=='m') return new ClientDeposit(d.getId(),listRes);
        else if(me=='e')return new ClientDeposit(d.getId()+3,listRes);
        else return null;
    }
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
    public static Map<Resource,Integer> strongboxToClientStrongbox(Map<ResourceType,Integer> s){
        Map<Resource,Integer> strongbox=new HashMap<>();
        for (Map.Entry<ResourceType, Integer> entry : s.entrySet()) {
            strongbox.put(Utilities.resourceTypeToResource(entry.getKey()),entry.getValue());
        }
        return strongbox;
    }

    public static String MORTitle() {
        String sb = "--------------------------------------------------------------\n" +
                " ╔╦╗╔═╗╔═╗╔╦╗╔═╗╦═╗╔═╗ ╔═╗╔═╗ ╦═╗╔═╗╔╗╔╔═╗╦╔═╗╔═╗╔═╗╔╗╔╔═╗╔═╗\n" +
                " ║║║╠═╣╚═╗ ║ ║╣ ╠╦╝╚═╗ ║ ║║╣  ╠╦╝║╣ ║║║╠═╣║╚═╗╚═╗╠═╣║║║║  ║╣ \n" +
                " ╩ ╩╩ ╩╚═╝ ╩ ╚═╝╩╚═╚═╝ ╚═╝╩   ╩╚═╚═╝╝╚╝╩ ╩╩╚═╝╚═╝╩ ╩╝╚╝╚═╝╚═╝ \n" +
                "--------------------------------------------------------------\n";
        return sb;
    }
}
