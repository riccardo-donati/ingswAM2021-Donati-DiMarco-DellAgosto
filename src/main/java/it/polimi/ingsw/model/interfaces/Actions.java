package it.polimi.ingsw.model.interfaces;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.enums.ResourceType;

import java.util.List;

public interface Actions {
    void buyAtMarket(char rc,int index);
    void moveResource(int id1, int id2);
    void addResourceInDeposit(Integer id,ResourceType res);
    void discardResource(ResourceType res);
    void playLeader(LeaderCard ld);
    void discardLeader(LeaderCard ld);
    void chooseLeaders(List<LeaderCard> leaderCards);
    void buyCard(DevelopmentCard cd,Integer slot);
    void toggleProduction(Production p);
    void activateProduction();
    void transformWhiteIn();
    void substituteUnknownInBaseProductionInput(ResourceType res);
    void substituteUnknownInBaseProductionOutput(ResourceType res);
    void substituteUnknownInExtraProductionOutput(ResourceType res,Integer id);
    void substituteUnknownInExtraProductionInput(ResourceType res,Integer id);
    void pickUpResourceFromWarehouse(Integer id);
    void pickUpResourceFromStrongbox(Integer id);
    void revertPickUp();

}
