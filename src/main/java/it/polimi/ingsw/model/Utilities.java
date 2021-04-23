package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.FullSpaceException;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.exceptions.UnknownNotFoundException;
import it.polimi.ingsw.model.interfaces.BoardObserver;
import it.polimi.ingsw.model.interfaces.Marble;
import it.polimi.ingsw.model.interfaces.Requirement;
import it.polimi.ingsw.model.interfaces.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class Utilities {

    /**
     * Merges two Map<ResourceType, Integer> objects
     * @param mainMap map that is modified
     * @param mapToAdd map that is merged to the first one
     */
    protected static void mergeResourceTypeMaps(Map<ResourceType, Integer> mainMap, Map<ResourceType, Integer> mapToAdd) {
        for (ResourceType resourceType : mapToAdd.keySet())
            mainMap.merge(resourceType, mapToAdd.get(resourceType), Integer::sum);
    }

    /**
     * changes an unknown in the target map into a resource type passed as parameter and tracks the change with the history map
     * @param map map with the unknown that will be substituted
     * @param history map that keeps track of the substitutions
     * @param resourceType resource type unknown will be changed into
     * @throws UnknownNotFoundException thrown if the map doesn't contain unknowns
     */
    protected static void replaceUnknown(Map<ResourceType, Integer> map, List<ResourceType> history, ResourceType resourceType) throws UnknownNotFoundException {
        if (map.containsKey(ResourceType.UNKNOWN)) {
            int quantity = map.get(ResourceType.UNKNOWN);
            if (quantity == 1)
                map.remove(ResourceType.UNKNOWN);
            else if (quantity > 1)
                map.replace(ResourceType.UNKNOWN, quantity - 1);
            map.merge(resourceType, 1, Integer::sum);
            history.add(resourceType);
        } else throw new UnknownNotFoundException();
    }

    /**
     * given a map and a list, for each resource type in the list adds an unknown in the map and removes a resource type from the map
     * @param map map that will be reset
     * @param history history of the changes of the map
     */
    protected static void revertUnknown(Map<ResourceType, Integer> map, List<ResourceType> history) {
        for (ResourceType resourceType : history) {
            map.merge(ResourceType.UNKNOWN, 1, Integer::sum);
            if (map.get(resourceType) == 1)
                map.remove(resourceType);
            else if (map.get(resourceType) > 1)
                map.replace(resourceType, map.get(resourceType) - 1);
        }
        history.clear();
    }

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
    public static void fillDeposits(Player p,boolean warehouse,boolean extradepots) throws IllegalResourceException, FullSpaceException {
        Map<ResourceType, Integer> resMap=new HashMap<>();
        resMap.put(ResourceType.YELLOW,999);
        resMap.put(ResourceType.BLUE,999);
        resMap.put(ResourceType.GREY,999);
        resMap.put(ResourceType.VIOLET,999);
        p.getBoard().depositInStrongbox(resMap);

        if(warehouse) {
            p.getBoard().getWarehouse().getMaindepot().clear();
            p.getBoard().getWarehouse().getMaindepot().add(new Deposit(1,ResourceType.EMPTY,1));
            p.getBoard().getWarehouse().getMaindepot().add(new Deposit(2,ResourceType.EMPTY,2));
            p.getBoard().getWarehouse().getMaindepot().add(new Deposit(3,ResourceType.EMPTY,3));
            p.getBoard().getWarehouse().getMaindepot().get(0).addResource(ResourceType.YELLOW);
            p.getBoard().getWarehouse().getMaindepot().get(1).addResource(ResourceType.BLUE);
            p.getBoard().getWarehouse().getMaindepot().get(1).addResource(ResourceType.BLUE);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);
            p.getBoard().getWarehouse().getMaindepot().get(2).addResource(ResourceType.GREY);

            if(extradepots) {
                p.getBoard().getWarehouse().addExtraDepot(ResourceType.VIOLET);
                p.getBoard().getWarehouse().addExtraDepot(ResourceType.YELLOW);
                p.getBoard().getWarehouse().addExtraDepot(ResourceType.GREY);
                p.getBoard().getWarehouse().addExtraDepot(ResourceType.BLUE);

                p.getBoard().getWarehouse().getExtradepots().get(0).addResource(ResourceType.VIOLET);
                //p.getBoard().getWarehouse().visualize();
            }
        }
    }
}
