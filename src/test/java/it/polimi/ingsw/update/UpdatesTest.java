package it.polimi.ingsw.update;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.Warehouse;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientBoard;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.messages.updates.SlotUpdate;
import it.polimi.ingsw.network.messages.updates.Update;
import it.polimi.ingsw.network.messages.updates.WarehouseUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UpdatesTest {
    ClientModel cm=new ClientModel();

    @BeforeEach
    public void setup(){
        cm.setCurrentNickname("rick");
        cm.setNickname("rick");
        cm.putBoard("rick",new ClientBoard());
        cm.setPlayersOrder(new ArrayList<>());
        cm.getCardMatrix().setDCard(Utilities.initializeCardMatrix(cm.getDevelopmentCards()));
        cm.getMarket().initializeMarbles();
        System.out.println(cm);
    }
    @Test
    public void TestSlotUpdate(){
        //get of the top development card in the stack 2,3
        DevelopmentCard dc=cm.getCardMatrix().getdCard()[2][3].get(cm.getCardMatrix().getdCard()[2][3].size()-1);
        SlotUpdate su=new SlotUpdate(1,2,3);
        su.update(cm);
        assertEquals(dc,cm.getCurrentBoard().getSlots().get(1).get(cm.getCurrentBoard().getSlots().get(1).size()-1));
        assertEquals(3,cm.getCardMatrix().getdCard()[2][3].size());
        System.out.println(cm);
    }
    @Test
    public void TestWarehouseUpdate(){
        List<Resource> list1=new ArrayList<>();
        List<Resource> list2=new ArrayList<>();
        List<Resource> list3=new ArrayList<>();
        list1.add(Resource.COIN);
        list2.add(Resource.STONE);
        list3.add(Resource.SHIELD);
        list3.add(Resource.SHIELD);
        list3.add(Resource.SHIELD);

        ClientDeposit cd1=new ClientDeposit(1,list1);
        ClientDeposit cd2=new ClientDeposit(2,list2);
        ClientDeposit cd3=new ClientDeposit(3,list3);

        List<ClientDeposit> clientDeposits=new ArrayList<>();
        clientDeposits.add(cd1);
        clientDeposits.add(cd2);
        clientDeposits.add(cd3);

        WarehouseUpdate whu=new WarehouseUpdate(clientDeposits);
        whu.update(cm);
        System.out.println(cm);
    }
}
