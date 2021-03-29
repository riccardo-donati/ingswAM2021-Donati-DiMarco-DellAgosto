package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.interfaces.Marble;

import java.util.Objects;

public class ResourceMarble implements Marble {
    private ResourceType type;

    public ResourceMarble(ResourceType t){
        type=t;
    }
    @Override
    public void action(Player p) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceMarble that = (ResourceMarble) o;
        return type == that.type;
    }

}
