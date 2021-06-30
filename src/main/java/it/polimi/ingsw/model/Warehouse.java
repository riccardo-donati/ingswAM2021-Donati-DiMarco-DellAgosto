package it.polimi.ingsw.model;

import com.google.gson.annotations.Expose;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.BoardObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Warehouse {
    @Expose
    private List<Deposit> maindepot;
    @Expose
    private List<Deposit> extradepots;
    @Expose
    private Map<ResourceType,Integer> pendingResources;
    private List<BoardObserver> observers=new ArrayList<>();
    @Expose
    private int nDeposit=0;

    public List<BoardObserver> getObservers() {
        return observers;
    }

    /**
     * initializing the maindepot with empty resourcetypes and
     * the map with the 4 resourcetype we want
     */
    protected Warehouse(){
        maindepot=new ArrayList<>();
        try {
            nDeposit++;
            maindepot.add(new Deposit(nDeposit,ResourceType.EMPTY,1));
            nDeposit++;
            maindepot.add(new Deposit(nDeposit,ResourceType.EMPTY,2));
            nDeposit++;
            maindepot.add(new Deposit(nDeposit,ResourceType.EMPTY,3));

        } catch (IllegalResourceException e) {
            e.printStackTrace();
        }
        extradepots=new ArrayList<>();
        pendingResources=new HashMap<>();
        pendingResources.put(ResourceType.GREY,0);
        pendingResources.put(ResourceType.BLUE,0);
        pendingResources.put(ResourceType.VIOLET,0);
        pendingResources.put(ResourceType.YELLOW,0);
        pendingResources.put(ResourceType.WHITE,0);
        pendingResources.put(ResourceType.UNKNOWN,0);
    }

    /**
     *
     * @param res is the resource that i want to add
     * @throws IllegalResourceException if you try to add an illegal resource(RED, UNKNOWN, EMPTY, WHITE)
     */
    protected void addResourceInPending(ResourceType res) throws IllegalResourceException {
        if(pendingResources.containsKey(res)){
            pendingResources.replace(res,pendingResources.get(res)+1);
        }else{
            throw new IllegalResourceException();
        }
    }

    /**
     * get of maindepot
     * @return maindepot
     */
    public List<Deposit> getMaindepot() {
        return maindepot;
    }

    /**
     * counts all the resources in the warehouse
     * @return map of all the resources in the warehouse
     */
    protected Map<ResourceType, Integer> getTotalResources() {
        Map<ResourceType, Integer> tempMap = new HashMap<>();
        List<Deposit> temp = new ArrayList<>(maindepot);
        temp.addAll(extradepots);
        for (Deposit deposit : temp) {
            int counter = 0;
            for (ResourceType resourceType : deposit.getSpace())
                if (!resourceType.equals(ResourceType.EMPTY))
                    counter++;
            if (tempMap.containsKey(deposit.getType()))
                tempMap.replace(deposit.getType(), tempMap.get(deposit.getType()) + counter);
            else tempMap.put(deposit.getType(), counter);
        }
        return tempMap;
    }

    /**
     * counts the points generated from the resources in the warehouse and in the leaderdeposits
     * @return the points
     */
    protected Integer countWarehouseResource(){
        return getTotalResources().values().stream().reduce(0, Integer::sum);
    }

    /**
     *
     * @param res is the resource of the deposit that i want to create
     * @throws IllegalResourceException if you try to add and illegal deposit
     */
    protected void addExtraDepot(ResourceType res) throws IllegalResourceException {
        if(res==ResourceType.RED || res==ResourceType.EMPTY || res==ResourceType.UNKNOWN || res==ResourceType.WHITE){
            throw new IllegalResourceException();
        }else{
            nDeposit++;
            extradepots.add(new Deposit(nDeposit,res,2));
        }
    }

    /**
     *
     * @return the list extradepots
     */
    public List<Deposit> getExtradepots() {
        return extradepots;
    }

    protected void chooseResourceToDeposit(Integer id,ResourceType res) throws UnknownNotFoundException, FullSpaceException, IllegalResourceException {
        if(pendingResources.get(ResourceType.UNKNOWN)<=0) throw new UnknownNotFoundException();
        if(!pendingResources.containsKey(res) || res==ResourceType.WHITE || res==ResourceType.EMPTY) throw new IllegalResourceException();
        pendingResources.replace(ResourceType.UNKNOWN,pendingResources.get(ResourceType.UNKNOWN)-1);
        pendingResources.replace(res,pendingResources.get(res)+1);
        try {
            addResourceInDeposit(id, res);
        }catch(IllegalResourceException e){
            pendingResources.replace(ResourceType.UNKNOWN,pendingResources.get(ResourceType.UNKNOWN)+1);
            pendingResources.replace(res,pendingResources.get(res)-1);
            throw new IllegalResourceException();
        }
    }
    /**
     * add a resource from pending to a deposit
     * @param id is the id of the deposit
     * @param res is the type of the resource
     */
    protected void addResourceInDeposit(Integer id,ResourceType res) throws IllegalResourceException, FullSpaceException {
        Integer n=pendingResources.get(res);
        if(n==null || res==ResourceType.WHITE || res==ResourceType.EMPTY)throw new IllegalResourceException("No such resource in pending");
        if(n>0) {
            if (id <= 3) {
                for(Deposit d : maindepot){
                    if(d.getType()==res && !d.getId().equals(id)){
                        throw new IllegalResourceException();    //a shelf already has this resource type as a type
                    }
                }
                maindepot.get(id - 1).addResource(res);
            } else {
                extradepots.get(id - 4).addResource(res);
            }
            pendingResources.replace(res,n-1);
        }
        else{
            throw new IllegalResourceException("No pending resource of this type");
        }
    }

    /**
     * move a resource from a deposit to another
     * or swap the resources if the second deposit is not empty
     * @param id1 is the id of the from deposit
     * @param id2 is the id of the to deposit
     */
    protected void moveResource(Integer id1,Integer id2) throws FullSpaceException, IllegalResourceException, NonEmptyException {
        Deposit d1;
        Deposit d2;
        if(id1<=3) d1=maindepot.get(id1-1);
        else d1=extradepots.get(id1-4);
        if(id2<=3) d2=maindepot.get(id2-1);
        else d2=extradepots.get(id2-4);

        int cont1=0;
        int cont2=0;
        for(int i=0;i<d1.getDimension();i++){
            if(d1.getSpace()[i]!=ResourceType.EMPTY) cont1++;
        }
        for(int i=0;i<d2.getDimension();i++){
            if(d2.getSpace()[i]!=ResourceType.EMPTY) cont2++;
        }

        //swap all the Resource in the deposit if possibile
        if(cont1>0 && cont2>0 && cont1<=d2.getDimension() && cont2<=d1.getDimension() && d1.getType()!=d2.getType() && id1<=3 && id2<=3){
            List<ResourceType> res1=new ArrayList<>();
            List<ResourceType> res2=new ArrayList<>();
            ResourceType type1=d1.getType();
            ResourceType type2=d2.getType();
            for(int i=0;i<cont1;i++){
                try {
                    res1.add(removeResourceFromDeposit(d1.getId()));
                } catch (DepositNotExistingException e) {
                    e.printStackTrace();
                }
                //res1.add(d1.removeResource());
            }
            for(int i=0;i<cont2;i++){
                try {
                    res2.add(removeResourceFromDeposit(d2.getId()));
                } catch (DepositNotExistingException e) {
                    e.printStackTrace();
                }
                //res2.add(d2.removeResource());
            }
            d1.changeType(type2);
            d2.changeType(type1);
            for(ResourceType r : res1){
                if(r!=ResourceType.EMPTY)
                    d2.addResource(r);
            }
            for(ResourceType r : res2){
                if(r!=ResourceType.EMPTY)
                    d1.addResource(r);
            }
        }
        //swapping between maindepots -> swap everything
        if(cont1>1 && cont2==0 && id1<=3 && id2<=3 ){
            if(d2.getDimension()<cont1) throw new FullSpaceException();
            ResourceType res=d1.getType();
            for(int i=0;i<cont1;i++){
                try {
                    removeResourceFromDeposit(d1.getId());
                } catch (DepositNotExistingException e) {
                    e.printStackTrace();
                }
                //d1.removeResource();
            }
            for(int i=0;i<cont1;i++){
                d2.addResource(res);
            }
        }
        //swap between extradeposit
        if(id1>3 && id2>3 && d1.getType()==d2.getType() && cont1>0 && cont2>0 && d1.getDimension().equals(d2.getDimension())){
            ResourceType res=d1.getType();
            for(int i=0;i<cont1;i++){
                try {
                    removeResourceFromDeposit(d1.getId());
                } catch (DepositNotExistingException e) {
                    e.printStackTrace();
                }
                //d1.removeResource();
            }
            for(int i=0;i<cont1;i++){
                d2.addResource(res);
            }
        }
        //if the second deposit has 0 resource or the second is the same type of the first --> move
        if(cont1==1 && cont2==0 && d2.getType()==ResourceType.EMPTY && id1<=3 && id2<=3 || cont1>0 && d2.freeSpaces()>0 && id1>3 && !checkResourceInWarehouse(d1.getType()) || cont1>0 && d2.freeSpaces()>0 && d1.getType()==d2.getType()){
            ResourceType r1=d1.getType();
            d2.addResource(r1);
            try {
                removeResourceFromDeposit(d1.getId());
            } catch (DepositNotExistingException e) {
                e.printStackTrace();
            }
            //d1.removeResource();
        }


    }

    /**
     * function for checking if a resource type is in the maindepot
     * @param res is the ResourceType
     * @return true if found, false if not
     */
    protected boolean checkResourceInWarehouse(ResourceType res){
        for(Deposit d : maindepot) {
            if (d.getType() == res) return true;
        }
        return false;

    }

    /**
     * discard a pending resource
     * @param res is the type of the resource i want to discard
     * @throws IllegalResourceException when there are no resources of res type in pending
     */
    protected void discardResource(ResourceType res) throws IllegalResourceException, DepositableResourceException {
        int n=pendingResources.get(res);
        if(n>0){
            boolean canDiscard = true;
            for(Deposit d : maindepot){
                if(d.getType()==ResourceType.EMPTY) canDiscard = false;
                else if(d.getType()==res){
                    canDiscard = d.freeSpaces() == 0;
                    break;
                }
            }
            for(Deposit d : extradepots){
                if((d.getType()==res || d.getType()==ResourceType.EMPTY) && d.freeSpaces()>0){
                    canDiscard=false;
                }
            }
            if(canDiscard) {
                pendingResources.replace(res, n - 1);
                notifyObservers();
            }else throw new DepositableResourceException();
        }else{
            throw new IllegalResourceException("No pending resource of this type");
        }
    }

    /**
     * register an observer to the list
     * @param obs observer to add
     */
    protected void addObserver(BoardObserver obs){
        observers.add(obs);
    }

    /**
     * notify the registered obsrvers
     */
    protected void notifyObservers(){
        for(BoardObserver o : observers){
            o.updateDiscard(this);
        }
    }

    /**
     * visualize in console the state of the warehouse, useful for debugging
     */
    protected void visualize(){
        System.out.println("PENDING");
        for (Map.Entry<ResourceType, Integer> entry : pendingResources.entrySet()) {
            System.out.println(entry.getKey() + "|" + entry.getValue());
        }
        System.out.println("--------\nMAIN:");
        for(Deposit d : maindepot){
            System.out.print(d.getId()+"["+d.getType()+"]||");
            for(int i=0;i<d.getDimension();i++){
                System.out.print(d.getSpace()[i]+"|");
            }
            System.out.println("\n");
        }
        System.out.println("--------\nEXTRA");
        for(Deposit d : extradepots){
            System.out.print(d.getId()+"["+d.getType()+"]||");
            for(int i=0;i<d.getDimension();i++){
                System.out.print(d.getSpace()[i]+"|");
            }
            System.out.println("\n");
        }
    }
    //----------------------------------------------------

    /**
     * This method replace a white resource in pending in qt selected resources
     * @param res is the destination ResourceType
     * @param qt is the quantity of the converted resource
     * @throws NoWhiteResourceException if there aren't any white resource in pending
     */
    protected void replaceWhiteFromPending(ResourceType res,Integer qt) throws NoWhiteResourceException {
        if(pendingResources.containsKey(ResourceType.WHITE) && pendingResources.get(ResourceType.WHITE)>0){
            pendingResources.replace(ResourceType.WHITE,pendingResources.get(ResourceType.WHITE)-1);
            for(int i=0;i<qt;i++)
                pendingResources.replace(res,pendingResources.get(res)+1);
        }else{
            throw new NoWhiteResourceException();
        }
    }

    protected Map<ResourceType, Integer> getPendingResources() { return pendingResources; }

    /**
     * transforms the pending resources map into a list
     * @return the pending resources map as a list
     */
    protected List<ResourceType> getPendingList() {
        List<ResourceType> pending = new ArrayList<>();
        for (ResourceType key : pendingResources.keySet()){
            for (int i = 0; i < pendingResources.get(key); i++) {
                pending.add(key);
            }
        }
        return pending;
    }

    /**
     * count the resource in pending
     * @return the sum of the resource of the map
     */
    protected int countPendingResources(){
        int sum=0;
        for (Map.Entry<ResourceType, Integer> entry : pendingResources.entrySet()) {
            sum+=entry.getValue();
        }
        return sum;
    }

    /**
     * remove a resource from the selected deposit
     * @param id is the id of the deposit
     * @return the removed resource
     */
    protected ResourceType removeResourceFromDeposit(Integer id) throws DepositNotExistingException, NonEmptyException {
        Deposit d;
        ResourceType type = ResourceType.EMPTY;

        if(id < 1 || id > nDeposit)
            throw new DepositNotExistingException();

        if (id <= 3) {
            d = getMaindepot().get(id - 1);
        } else {
            d = getExtradepots().get(id - 4);
            type=d.getType();
        }

        ResourceType res=d.removeResource();
        if(d.getType()==ResourceType.EMPTY)
            d.changeType(type);
        return res;
    }
}
