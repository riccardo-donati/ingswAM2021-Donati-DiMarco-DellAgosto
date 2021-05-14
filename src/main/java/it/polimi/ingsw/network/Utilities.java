package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.interfaces.Requirement;
import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.server.ClientHandler;
import it.polimi.ingsw.network.server.Server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utilities {

    public static Integer loadServerPortNumber() throws FileNotFoundException, NullPointerException {
        JsonReader reader = new JsonReader(new FileReader("src/main/resources/json/serverSettings.json"));
        Map<String, Integer> map = new Gson().fromJson(reader, new TypeToken<Map<String, Integer>>() {}.getType());
        return map.get("portNumber");
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
        return builder.create();
    }

    public static Gson initializeGsonLoadAndSave(){
        GsonBuilder builder = new GsonBuilder();
        builder.enableComplexMapKeySerialization();
        return builder.excludeFieldsWithoutExposeAnnotation().create();
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
        sb.append(Color.ANSI_GREEN.escape() + "COLOR: " + Utilities.modelColorToClientColor(d.getColor()).escape() + "■" + Color.RESET + " | ");
        sb.append(Color.ANSI_GREEN.escape() + "LEVEL: " + Color.RESET + d.getLevel() + " | ");
        sb.append(Color.ANSI_GREEN.escape() + "COST:" + Color.RESET);
        List<ResourceRequirement> rrList = d.getCost();
        for (ResourceRequirement rr : rrList) {
            sb.append(rr.getQuantity() + Utilities.resourceTypeToResource(rr.getResource()).label + " ");
        }
        sb.append("| ");
        sb.append(Color.ANSI_GREEN.escape() + "PRODUCTION: " + Color.RESET);
        Map<ResourceType, Integer> input = d.getProd().getInput();
        Map<ResourceType, Integer> output = d.getProd().getOutput();
        sb.append("Input:");
        for (Map.Entry<ResourceType, Integer> entry : input.entrySet()) {
            sb.append(entry.getValue() + Utilities.resourceTypeToResource(entry.getKey()).label + " ");
        }
        sb.append(Color.ANSI_RED.escape() + "⇒" + Color.RESET + " Output:");
        for (Map.Entry<ResourceType, Integer> entry : output.entrySet()) {
            sb.append(entry.getValue() + Utilities.resourceTypeToResource(entry.getKey()).label + " ");
        }
        sb.append("|"+Color.ANSI_GREEN.escape() + " POINTS: " + Color.ANSI_YELLOW.escape() + d.getPoints() + Color.RESET);
        sb.append(" ]");

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
                sb.append(lcr.getQuantity()+"("+Utilities.modelColorToClientColor(lcr.getColor()).escape()+"■"+Color.RESET+" lvl"+lcr.getLevel()+")");
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




}
