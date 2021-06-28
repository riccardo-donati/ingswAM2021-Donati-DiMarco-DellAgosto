package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.network.client.CLI.enums.Color;
import it.polimi.ingsw.network.client.CLI.enums.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientDeposits {
    private final List<Shelf> shelves = new ArrayList<>();
    private Map<Resource, Integer> strongbox = new HashMap<>();
    private Map<Resource, Integer> handResources = new HashMap<>();

    /**
     * constructor - sets up empty strongbox and warehouse shelves
     */
    public ClientDeposits(){
        strongbox.put(Resource.COIN,0);
        strongbox.put(Resource.SHIELD,0);
        strongbox.put(Resource.STONE,0);
        strongbox.put(Resource.SERVANT,0);
        shelves.add(new Shelf(1,1));
        shelves.add(new Shelf(2,2));
        shelves.add(new Shelf(3,3));
    }

    public void setHandResources(Map<Resource, Integer> handResources) {
        this.handResources = handResources;
    }

    /**
     * reset the handResources value to 0
     */
    public void clearHandResources(){
        for (Map.Entry<Resource, Integer> entry : handResources.entrySet()) {
            entry.setValue(0);
        }
    }
    public Map<Resource, Integer> getStrongbox() {
        return strongbox;
    }

    public void setStrongbox(Map<Resource, Integer> strongbox) {
        this.strongbox = strongbox;
    }

    public void putResourceInHand(Resource res){
        if (handResources.get(res) == null)
            handResources.put(res, 1);
        else handResources.replace(res, handResources.get(res)+1);
    }

    public void clearResourcesInHand(){
        handResources.clear();
    }

    public Map<Resource, Integer> getHandResources() {
        return handResources;
    }

    /**
     *
     * @return a readable string for the handResources
     */
    public String stringifyHandResources(){
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape()).append("RESOURCES IN HAND: ").append(Color.RESET).append("[ ");
        for (Map.Entry<Resource, Integer> entry : handResources.entrySet())
            sb.append(entry.getValue()).append(entry.getKey().label).append(" ");
        sb.append("]");
        return sb.toString();
    }

    public List<Shelf> getShelves() {
        return shelves;
    }

    public Shelf getShelf(int id){
        for(Shelf shelf : shelves){
            if (shelf.getId() == id) return shelf;
        }
        return null;
    }

    public void addShelf(Shelf shelf){
        shelves.add(shelf);
    }

    /**
     * deposits a single resource in the specified shelf
     * @param resource - resource to be added
     * @param id - shelf id
     */
    public void deposit(Resource resource, Integer id){
        Shelf shelf = getShelf(id);
        for (int i = 0; i < shelf.getSpaces().length; i++) {
            if (shelf.getSpaces()[i].equals(Resource.EMPTY)) {
                shelf.put(i, resource);
                return;
            }
        }
    }

    /**
     * clears the specified shelf and deposits a list of resource
     * @param resource - list of resources to be added
     * @param id - shelf id
     */
    public void deposit(List<Resource> resource, Integer id){
        Shelf s = getShelf(id);
        if (s != null && resource.size() <= s.getSpaces().length) {
            s.clear();
            for (int i = 0; i < resource.size(); i++) {
                s.put(i, resource.get(i));
            }
        }
    }

    /**
     * removes a resource from the specified shelf
     * @param id - shelf id
     */
    public Resource remove(Integer id){
        Shelf s = getShelf(id);
        for (int i = s.getSpaces().length - 1; i >= 0; i--)
            if (!s.getSpaces()[i].equals(Resource.EMPTY))
                return s.remove(i);
        return Resource.EMPTY;
    }

    /**
     * removes a resource from the strongbox
     * @param resource - resource to be removed
     */
    public Resource removeResourceFromStrongbox(Resource resource) {
        if(strongbox.get(resource) > 0) {
            strongbox.replace(resource, strongbox.get(resource) - 1);
            return resource;
        }
        return Resource.EMPTY;
    }

    /**
     * adds a resource to the strongbox
     * @param resource - resource to be added
     */
    public void putResourceInStrongbox(Resource resource){
        strongbox.merge(resource, 1, Integer::sum);
    }

    /**
     * transforms the warehouse, strongbox and extra deposits in a string
     * @return string that depicts the whole resource deposit
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Color.ANSI_PURPLE.escape()).append("WAREHOUSE:                      STRONGBOX:").append(Color.RESET).append("\n");
        int spacesbonus=0;
        for(Shelf s : shelves){
            if(s.getId()==4) sb.append("-----------------------------║\n");
            if(s.getId()==1)spacesbonus=5;
            if(s.getId()==2)spacesbonus=2;
            if(s.getId()==3)spacesbonus=0;
            sb.append("           ");
            sb.append(" ".repeat(spacesbonus));
            for(int i=0;i<s.getSpaces().length;i++){
                if(s.getId()==2)
                    sb.append("╔═══╗ ");
                else sb.append("╔═══╗");
            }
            //strongbox
            if(s.getId()==1) {
                sb.append("        ║");
                sb.append("   ").append(Resource.SHIELD.label).append(" : ").append(strongbox.get(Resource.SHIELD));
            }
            else if(s.getId()==2) {
                sb.append("    ║");
                sb.append("   ").append(Resource.STONE.label).append(" : ").append(strongbox.get(Resource.STONE));
            }
            else if(s.getId()==3) {
                sb.append("   ║");
            }else if(s.getId()>3){
                sb.append("        ║");
            }

            sb.append("\n");
            sb.append("Deposit ").append(s.getId()).append(": ");
            sb.append(" ".repeat(spacesbonus));
            for(int i=0;i< s.getSpaces().length;i++){
                if(s.getId()==2) sb.append("║ ").append(s.getSpaces()[i].label).append(" ║ ");
                else sb.append("║ ").append(s.getSpaces()[i].label).append(" ║");

            }

            //strongbox
            if(s.getId()==1) {
                sb.append("        ║");
                sb.append("   ").append(Resource.COIN.label).append(" : ").append(strongbox.get(Resource.COIN));
            }
            else if(s.getId()==2){
                sb.append("    ║");
            }
            else if(s.getId()==3)sb.append("   ║");
            else if(s.getId()>3){
                 sb.append("        ║");
            }
            sb.append("\n");
            sb.append("           ");
            sb.append(" ".repeat(spacesbonus));
            for(int i=0;i<s.getSpaces().length;i++){
                if(s.getId()==2)
                    sb.append("╚═══╝ ");
                else sb.append("╚═══╝");
            }

            //strongbox
            if(s.getId()==1) {
                sb.append("        ║");
                sb.append("   ").append(Resource.SERVANT.label).append(" : ").append(strongbox.get(Resource.SERVANT));
            }
            else if(s.getId()==2){
                sb.append("    ║");
            }
            else if(s.getId()==3)sb.append("   ║");
            else if(s.getId()>3){
                 sb.append("        ║");
             }
            sb.append("\n");
        }
        sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }
}
