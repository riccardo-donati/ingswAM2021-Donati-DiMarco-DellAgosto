package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullSpaceException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.interfaces.BoardObserver;
import it.polimi.ingsw.model.interfaces.Marble;
import it.polimi.ingsw.model.interfaces.Requirement;
import it.polimi.ingsw.model.interfaces.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public  class Utilities {
    /**
     * load a game from a json file
     * @param name is the name of the json file (just name without .json)
     * @param type is 's' for singleplayer or 'm' for multiplayer
     * @return the game loaded
     * @throws IOException if there is a problem with the file
     */
    public static Game loadGame(String name,char type) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Requirement.class, new InterfaceAdapter<Requirement>());
        builder.registerTypeAdapter(SpecialAbility.class, new InterfaceAdapter<SpecialAbility>());
        builder.registerTypeAdapter(Token.class, new InterfaceAdapter<Token>());
        builder.registerTypeAdapter(Marble.class, new InterfaceAdapter<Marble>());
        builder.registerTypeAdapter(BoardObserver.class, new InterfaceAdapter<BoardObserver>());
        builder.registerTypeAdapter(Game.class, new InterfaceAdapter<Game>());

        Gson gson = builder.create();
        FileReader fr = new FileReader("src/main/java/it/polimi/ingsw/model/json/gameState/"+name+".json");
        BufferedReader b;
        b=new BufferedReader(fr);
        String json=b.readLine();
        Game g;
        if(type=='m') g=gson.fromJson(json,Multiplayer.class);
        else {
            g=gson.fromJson(json,Singleplayer.class);
            g.getBlackCrossFaithPath().addObserver(g);
        }
        for(Player p : g.getPlayers()){
            p.getBoard().addObserver(g);
            p.getBoard().getFaithPath().addObserver(g);
            p.getBoard().getWarehouse().addObserver(g);
            if(p.getOrder().equals(g.getCurrPlayer().getOrder()) && p.getNickname().equals(g.getCurrPlayer().getNickname()))
                g.setCurrPlayer(p);
        }

        return g;
    }
    public static void fillDeposits(Player p,boolean warehouse) throws IllegalResourceException, FullSpaceException {
        Map<ResourceType, Integer> resMap=new HashMap<>();
        resMap.put(ResourceType.YELLOW,999);
        resMap.put(ResourceType.BLUE,999);
        resMap.put(ResourceType.GREY,999);
        resMap.put(ResourceType.VIOLET,999);
        p.getBoard().depositInStrongbox(resMap);

        if(warehouse) {
            p.getBoard().getWarehouse().getMaindepot().get(0).addResource(ResourceType.YELLOW);
            p.getBoard().getWarehouse().getMaindepot().get(1).addResource(ResourceType.BLUE);
            p.getBoard().getWarehouse().getMaindepot().get(1).addResource(ResourceType.BLUE);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);

            p.getBoard().getWarehouse().addExtraDepot(ResourceType.VIOLET);
            p.getBoard().getWarehouse().addExtraDepot(ResourceType.YELLOW);
            p.getBoard().getWarehouse().addExtraDepot(ResourceType.GREY);
            p.getBoard().getWarehouse().addExtraDepot(ResourceType.BLUE);

            p.getBoard().getWarehouse().getExtradepots().get(0).addResource(ResourceType.VIOLET);
            //p.getBoard().getWarehouse().visualize();
        }
    }
}
