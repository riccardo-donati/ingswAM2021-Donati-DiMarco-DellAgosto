package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.interfaces.Marble;

import java.util.Map;

public class WhiteMarble implements Marble {
    /**
     * if the player has:
     * - 1 white marble converter in the whiteTo list -> add the converted resource in pending of Warehouse
     * - 2 or more white marble converter -> add a WHITE resource in pending of Warehouse
     * - 0 white marble converter -> do nothing
     * @param p is the Player
     */
    @Override
    public void action(Player p) {
        if(p.getWhiteTo().size()==1){
            for (Map.Entry<ResourceType, Integer> entry : p.getWhiteTo().entrySet()) {
                try {
                    for(int i=0;i<entry.getValue();i++)
                         p.getBoard().getWarehouse().addResourceInPending(entry.getKey());
                    break;
                } catch (IllegalResourceException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        else if(p.getWhiteTo().size()>1){
            try {
                p.getBoard().getWarehouse().addResourceInPending(ResourceType.WHITE);
            } catch (IllegalResourceException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WhiteMarble;
    }
}
