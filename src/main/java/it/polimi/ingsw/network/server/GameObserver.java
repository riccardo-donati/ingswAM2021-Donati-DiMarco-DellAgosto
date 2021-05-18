package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.Result;

public interface GameObserver {
    void updateEndGameTriggered();
    void updatePopeFavors();
    void updateEndGameResult(Result result);
}
