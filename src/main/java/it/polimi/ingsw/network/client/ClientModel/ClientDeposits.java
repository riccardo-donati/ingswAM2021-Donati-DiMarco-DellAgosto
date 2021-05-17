package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientDeposits {
    List<Shelf> shelves = new ArrayList<>();
    Map<Resource, Integer> strongbox = new HashMap<>();

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
            if (shelf.getSpaces()[i] == null || shelf.getSpaces()[i].equals("  ")) {
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
    public void deposit(List<Resource> resource,Integer id){
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
    public void remove(Integer id){
        Shelf s = getShelf(id);
        for (int i = s.getSpaces().length - 1; i >= 0; i--) {
            if (s.getSpaces()[i]!=null && !s.getSpaces()[i].equals("  ")) {
                s.remove(i);
                return;
            }
        }
    }

    /**
     * removes a resource from the strongbox
     * @param resource - resource to be removed
     */
    public void removeResourceFromStrongbox(Resource resource){
        if(strongbox.get(resource) > 0)
            strongbox.replace(resource, strongbox.get(resource) - 1);
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
        sb.append(Color.ANSI_PURPLE.escape()+"WAREHOUSE:                      STRONGBOX:"+Color.RESET+"\n");
        int spacesbonus=0;
        for(Shelf s : shelves){
            if(s.getId()==4) sb.append("------------------------------ ║\n");
            if(s.getId()==1)spacesbonus=6;
            if(s.getId()==2)spacesbonus=3;
            if(s.getId()==3)spacesbonus=0;
            sb.append("           ");
            for(int j=0;j<spacesbonus;j++)sb.append(" ");
            for(int i=0;i<s.getSpaces().length;i++){
                sb.append("╔═ ══╗");
            }
            //strongbox
            if(s.getId()==1) {
                sb.append("            ║");
                sb.append("   "+Resource.SHIELD.label+" : "+strongbox.get(Resource.SHIELD));
            }
            else if(s.getId()==2) {
                sb.append("        ║");
                sb.append("   "+Resource.STONE.label+" : "+strongbox.get(Resource.STONE));
            }
            else if(s.getId()==3) {
                sb.append("    ║");
            }else if(s.getId()>3){
                sb.append("           ║");
            }

            sb.append("\n");
            sb.append("Deposit "+s.getId()+": ");
            for(int j=0;j<spacesbonus;j++) {
                sb.append(" ");
            }
            for(int i=0;i< s.getSpaces().length;i++){
                if(s.getSpaces()[i]==null) {
                    sb.append("║    ║");
                }
                else{
                    sb.append("║ "+s.getSpaces()[i]+" ║");
                }
            }

            //strongbox
            if(s.getId()==1) {
                sb.append("            ║");
                sb.append("   "+Resource.COIN.label+" : "+strongbox.get(Resource.COIN));
            }
            else if(s.getId()==2){
                sb.append("        ║");
            }
            else if(s.getId()==3)sb.append("    ║");
            else if(s.getId()>3){
                 sb.append("           ║");
            }
            sb.append("\n");
            sb.append("           ");
            for(int j=0;j<spacesbonus;j++)sb.append(" ");
            for(int i=0;i<s.getSpaces().length;i++){
                sb.append("╚══ ═╝");
            }

            //strongbox
            if(s.getId()==1) {
                sb.append("            ║");
                sb.append("   "+Resource.SERVANT.label+" : "+strongbox.get(Resource.SERVANT));
            }
            else if(s.getId()==2){
                sb.append("        ║");
            }
            else if(s.getId()==3)sb.append("    ║");
            else if(s.getId()>3){
                 sb.append("           ║");
             }
            sb.append("\n");
        }
        sb.append("═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }
}
