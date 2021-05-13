package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.InterfaceAdapter;
import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.SpecialAbility;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.interfaces.Requirement;
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




}
