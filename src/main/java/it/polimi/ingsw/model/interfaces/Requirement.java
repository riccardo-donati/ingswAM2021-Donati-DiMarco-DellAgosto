package it.polimi.ingsw.model.interfaces;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.enums.Color;

public interface Requirement {

    /**
     * checks whether the requirement is satisfied
     * @param board board that needs to be checked
     * @return 0 if the requirement is NOT satisfied, 1 otherwise
     */
    boolean check(Board board);
}
