package it.polimi.ingsw.network.client.ClientModel;

import it.polimi.ingsw.network.client.ClientModel.CLI.Color;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientDeposits {
    List<Shelf> shelves=new ArrayList<>();
    Map<Resource, Integer> strongbox=new HashMap<>();
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
        for(Shelf s : shelves){
            if(s.getId()==id)return s;
        }
        return null;
    }
    public void deposit(Resource r, Integer idD){
        Shelf s=getShelf(idD);
        for(int i=0;i<s.getSpaces().length;i++){
            if(s.getSpaces()[i]==null || s.getSpaces()[i].equals("  ")) {
                s.put(i, r);
                return;
            }
        }
    }
    public void deposit(List<Resource> r,Integer idD){
        Shelf s=getShelf(idD);
        if(s!=null && r.size()<=s.getSpaces().length){
            s.clear();
            for(int i=0;i<r.size();i++){
                s.put(i,r.get(i));
            }
            return;
        }
    }
    public void removeResourceFromStrongbox(Resource res){
        if(strongbox.get(res)!=0){
            strongbox.replace(res,strongbox.get(res)-1);
        }
    }
    public void putResourceInStrongbox(Resource res){
        if(strongbox.get(res)!=null){
            strongbox.replace(res,strongbox.get(res)+1);
        }else{
            strongbox.put(res,1);
        }
    }
    public void addShelf(Shelf s){
        shelves.add(s);
    }
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
