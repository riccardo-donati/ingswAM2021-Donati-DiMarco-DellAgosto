package it.polimi.ingsw.update;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.IllegalResourceException;
import it.polimi.ingsw.network.Utilities;
import it.polimi.ingsw.network.client.ClientModel.CLI.ClientPopeFavorState;
import it.polimi.ingsw.network.client.ClientModel.CLI.Resource;
import it.polimi.ingsw.network.client.ClientModel.ClientBoard;
import it.polimi.ingsw.network.client.ClientModel.ClientDeposit;
import it.polimi.ingsw.network.client.ClientModel.ClientModel;
import it.polimi.ingsw.network.client.ClientModel.Shelf;
import it.polimi.ingsw.network.messages.PendingResourcesMessage;
import it.polimi.ingsw.network.messages.updates.*;
import it.polimi.ingsw.network.server.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UpdatesTest {
    ClientModel cm=new ClientModel();

    @BeforeEach
    public void setup(){
        cm.setCurrentNickname("rick");
        cm.setNickname("rick");
        cm.putBoard("rick",new ClientBoard());
        cm.putBoard("dona",new ClientBoard());
        cm.setPlayersOrder(new ArrayList<>());
        cm.getCardMatrix().setCards(Utilities.initializeCardMatrix(cm.getDevelopmentCards()));
        cm.getMarket().initializeMarbles();
        //System.out.println(cm);
    }
    @Test
    public void TestSlotUpdate(){
        //get of the top development card in the stack 2,3
        DevelopmentCard dc=cm.getCardMatrix().getCards()[2][3].get(cm.getCardMatrix().getCards()[2][3].size()-1);
        SlotUpdate su=new SlotUpdate(1,2,3);
        su.update(cm);
        assertEquals(dc,cm.getCurrentBoard().getSlots().get(1).get(cm.getCurrentBoard().getSlots().get(1).size()-1));
        assertEquals(3,cm.getCardMatrix().getCards()[2][3].size());
        //System.out.println(cm);
    }
    @Test
    public void TestWarehouseUpdate(){
        cm.getCurrentBoard().getDeposits().addShelf(new Shelf(2,4));
        List<Resource> list1=new ArrayList<>();
        List<Resource> list2=new ArrayList<>();
        List<Resource> list3=new ArrayList<>();
        List<Resource> list4=new ArrayList<>();
        list1.add(Resource.COIN);
        list2.add(Resource.STONE);
        list3.add(Resource.SHIELD);
        list3.add(Resource.SHIELD);
        list3.add(Resource.SHIELD);
        list4.add(Resource.SERVANT);

        ClientDeposit cd1=new ClientDeposit(1,list1);
        ClientDeposit cd2=new ClientDeposit(2,list2);
        ClientDeposit cd3=new ClientDeposit(3,list3);
        ClientDeposit cd4=new ClientDeposit(4,list4);

        List<ClientDeposit> clientDeposits=new ArrayList<>();
        clientDeposits.add(cd1);
        clientDeposits.add(cd2);
        clientDeposits.add(cd3);
        clientDeposits.add(cd4);

        Map<Resource,Integer> strongbox=new HashMap<>();

        DepositsUpdate whu=new DepositsUpdate(clientDeposits,strongbox);
        whu.update(cm);
        System.out.println(cm);
    }
    @Test
    public void TestToggleProductionUpdate() throws IllegalResourceException {
        Production p1=new Production();
        p1.addInput(ResourceType.UNKNOWN,1);
        p1.addInput(ResourceType.GREY,1);
        p1.addOutput(ResourceType.RED,2);
        p1.addOutput(ResourceType.YELLOW,1);

        Production p2=new Production();
        p2.addInput(ResourceType.BLUE,3);
        p2.addOutput(ResourceType.UNKNOWN,2);

        List<Production> activeProductions=new ArrayList<>();
        activeProductions.add(p1);
        activeProductions.add(p2);

        ToggleProductionUpdate tpu=new ToggleProductionUpdate(activeProductions);
        tpu.update(cm);
        System.out.println("Active Productions: ");
        for(Production p : cm.getCurrentBoard().getActiveProductions()){
            System.out.print("["+Utilities.stringify(p)+"]");
        }
    }
    @Test
    public void TestPlayLeader(){
        cm.getCurrentBoard().getLeadersInHand().add(cm.getLeaderCard("5L"));
        System.out.println(cm);
        PlayLeaderUpdate plu=new PlayLeaderUpdate(0);
        assertEquals(cm.getLeaderCard("5L"),cm.getCurrentBoard().getLeadersInHand().get(0));
        plu.update(cm);
        System.out.println(cm.getCurrentBoard().stringifyLeaders());
        assertEquals(cm.getLeaderCard("5L"),cm.getCurrentBoard().getLeadersInBoard().get(0));
    }
    @Test
    public void TestUnknownProductions(){
        cm.getCurrentBoard().addExtraProd(ResourceType.YELLOW);
        cm.getCurrentBoard().addExtraProd(ResourceType.GREY);
        //base prod input ? -> violet
        UnknownProductionUpdate upu1=new UnknownProductionUpdate(-1,ResourceType.VIOLET,'i');
        upu1.update(cm);
        System.out.println(cm.getCurrentBoard().stringifyProductions());
        UnknownProductionUpdate upu2=new UnknownProductionUpdate(1,ResourceType.BLUE,'o');
        upu2.update(cm);
        System.out.println(cm.getCurrentBoard().stringifyProductions());
        assertEquals(1,cm.getCurrentBoard().getBaseProduction().getInput().get(ResourceType.VIOLET));
        assertEquals(1,cm.getCurrentBoard().getExtraProductions().get(1).getOutput().get(ResourceType.BLUE));
    }
    @Test
    public void TestStrongBoxUpdate(){
        cm.getCurrentBoard().getDeposits().putResourceInStrongbox(Resource.SHIELD);
        cm.getCurrentBoard().getDeposits().putResourceInStrongbox(Resource.COIN);

        Map<Resource,Integer> strongbox=new HashMap<>();
        strongbox.put(Resource.SHIELD,2);
        strongbox.put(Resource.COIN,3);
        strongbox.put(Resource.FAITH,1);
        strongbox.put(Resource.SERVANT,0);
        strongbox.put(Resource.STONE,0);
        DepositsUpdate sbu=new DepositsUpdate(new ArrayList<>(),strongbox);
        System.out.println(cm.getCurrentBoard().getDeposits());
        assertEquals(1,cm.getCurrentBoard().getDeposits().getStrongbox().get(Resource.SHIELD));
        assertEquals(1,cm.getCurrentBoard().getDeposits().getStrongbox().get(Resource.COIN));
        sbu.update(cm);
        assertEquals(2,cm.getCurrentBoard().getDeposits().getStrongbox().get(Resource.SHIELD));
        assertEquals(3,cm.getCurrentBoard().getDeposits().getStrongbox().get(Resource.COIN));
        System.out.println(cm.getCurrentBoard().getDeposits());
    }

    @Test
    public void TestToggleDiscountUpdate(){
        cm.getCurrentBoard().addDiscount(ResourceType.YELLOW);
        cm.getCurrentBoard().addDiscount(ResourceType.GREY);
        ToggleDiscountUpdate tdu1=new ToggleDiscountUpdate(ResourceType.YELLOW);
        tdu1.update(cm);
        System.out.println(cm.getCurrentBoard().stringifyActiveDiscounts());
        assertEquals(ResourceType.YELLOW,cm.getCurrentBoard().getActiveDiscounts().get(0).getRes());

        ToggleDiscountUpdate tdu2=new ToggleDiscountUpdate(ResourceType.GREY);
        tdu2.update(cm);
        assertEquals(ResourceType.GREY,cm.getCurrentBoard().getActiveDiscounts().get(1).getRes());
        System.out.println(cm.getCurrentBoard().stringifyActiveDiscounts());

        tdu1.update(cm);
        assertEquals(ResourceType.GREY,cm.getCurrentBoard().getActiveDiscounts().get(0).getRes());
        System.out.println(cm.getCurrentBoard().stringifyActiveDiscounts());
    }

    @Test
    public void TestDiscardResource(){
        DiscardResourceUpdate dru=new DiscardResourceUpdate();
        //multiplayer
        dru.update(cm);
        assertEquals(0,cm.getCurrentBoard().getFaithPath().getPosition());
        assertEquals(1,cm.getBoards().get("dona").getFaithPath().getPosition());

        //singleplayer
        cm.getCurrentBoard().getFaithPath().setLorenzoPosition(0);
        dru.update(cm);
        assertEquals(0,cm.getCurrentBoard().getFaithPath().getPosition());
        assertEquals(1,cm.getCurrentBoard().getFaithPath().getLorenzoPosition());
    }

    @Test
    public void TestDiscardLeader(){
        cm.getCurrentBoard().getLeadersInHand().add(cm.getLeaderCard("10L"));
        DiscardLeaderUpdate dlu=new DiscardLeaderUpdate(0);
        dlu.update(cm);
        System.out.println(cm.getCurrentBoard().getFaithPath());
        System.out.println(cm.getCurrentBoard().stringifyLeaders());
        assertEquals(0,cm.getCurrentBoard().getLeadersInHand().size());
        assertEquals(1,cm.getCurrentBoard().getFaithPath().getPosition());

    }
    @Test
    public void TestUpdateFaithPath(){
        Map<String, Integer> faithPaths=new HashMap<>();
        faithPaths.put("rick",8);
        faithPaths.put("dona",10);
        Map<String,Map<Integer, ClientPopeFavorState>> popefavors=new HashMap<>();
        Map<Integer, ClientPopeFavorState> pfmap1=new HashMap<>();
        Map<Integer, ClientPopeFavorState> pfmap2=new HashMap<>();

        pfmap1.put(1,ClientPopeFavorState.ACTIVE);
        pfmap1.put(2,ClientPopeFavorState.DISCARDED);
        pfmap1.put(3,ClientPopeFavorState.UNACTIVE);

        pfmap2.put(1,ClientPopeFavorState.DISCARDED);
        pfmap2.put(2,ClientPopeFavorState.ACTIVE);
        pfmap2.put(3,ClientPopeFavorState.DISCARDED);

        popefavors.put("rick",pfmap1);
        popefavors.put("dona",pfmap2);

        FaithPathUpdate fpu=new FaithPathUpdate(faithPaths,popefavors,null);
        fpu.update(cm);
        System.out.println(cm.getBoards().get("rick").getFaithPath());
        System.out.println(cm.getBoards().get("dona").getFaithPath());
    }
    @Test
    public void TestPendingWithWhite(){
        List<ResourceType> pending=new ArrayList<>();
        pending.add(ResourceType.YELLOW);
        pending.add(ResourceType.UNKNOWN);
        PendingResourcesMessage prm =new PendingResourcesMessage(pending);
        System.out.println(prm.getMessage());

    }
}
