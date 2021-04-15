package it.polimi.ingsw.model.interfaces;

import it.polimi.ingsw.model.Warehouse;

public interface BoardObserver {
    void updateEndGame();
    void updatePopeFavor();
    void updateDiscard(Warehouse wh);
}
