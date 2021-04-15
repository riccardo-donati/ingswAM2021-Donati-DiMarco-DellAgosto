package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.model.interfaces.Marble;

import java.util.Objects;

public class ResourceMarble implements Marble {
    private ResourceType type;

    protected ResourceMarble(ResourceType t){
        type=t;
    }

    /**
     *
     * @param p target of the changes
     */
    @Override
    public void action(Player p) {
        try {
            p.getBoard().getWarehouse().addResourceInPending(type);
        } catch (IllegalResourceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceMarble that = (ResourceMarble) o;
        return type == that.type;
    }

}
