package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.InterfaceAdapter;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.server.Server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

}
