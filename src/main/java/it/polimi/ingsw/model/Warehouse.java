package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.interfaces.BoardObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Warehouse {
    private List<Deposit> maindepot;
    private List<Deposit> extradepots;
    private Map<ResourceType,Integer> pendingResources;
    private List<BoardObserver> observers=new ArrayList<>();

    private int nDeposit=0;

    /**
     * initializing the maindepot with empty resourcetypes and
     * the map with the 4 resourcetype we want
     */
    public Warehouse(){
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
    public void addResourceInPending(ResourceType res) throws IllegalResourceException {
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
    public Map<ResourceType, Integer> getTotalResources() {
        Map<ResourceType, Integer> tempMap = new HashMap<>();
        for (Deposit deposit : maindepot) {
            int counter = 0;
            for (ResourceType resourceType : deposit.getSpace())
                if (!resourceType.equals(ResourceType.EMPTY))
                    counter++;
            if (tempMap.containsKey(deposit.getType()))
                tempMap.replace(deposit.getType(), tempMap.get(deposit.getType()) + counter);
            else tempMap.put(deposit.getType(), counter);
        }
        for (Deposit deposit : extradepots) {
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
    public Integer countWarehouseResource(){
        int resCont=0;
        Map<ResourceType, Integer> totalResources = getTotalResources();
        for (ResourceType resourceType : totalResources.keySet())
            resCont += totalResources.get(resourceType);
        return resCont;
    }

    /**
     *
     * @param res is the resource of the deposit that i want to create
     * @throws IllegalResourceException if you try to add and illegal deposit
     */
    public void addExtraDepot(ResourceType res) throws IllegalResourceException {
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

    public void chooseResourceToDeposit(Integer id,ResourceType res) throws UnknownNotFindException, FullSpaceException, IllegalResourceException {
        if(pendingResources.get(ResourceType.UNKNOWN)<=0) throw new UnknownNotFindException();
        if(!pendingResources.containsKey(res) || res==ResourceType.WHITE || res==ResourceType.EMPTY) throw new IllegalResourceException();
        pendingResources.replace(ResourceType.UNKNOWN,pendingResources.get(ResourceType.UNKNOWN)-1);
        pendingResources.replace(res,pendingResources.get(res)+1);
        addResourceInDeposit(id,res);
    }
    /**
     * add a resource from pending to a deposit
     * @param id is the id of the deposit
     * @param res is the type of the resource
     */
    public void addResourceInDeposit(Integer id,ResourceType res) throws IllegalResourceException, FullSpaceException {
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
    public void moveResource(Integer id1,Integer id2) throws FullSpaceException, IllegalResourceException, NonEmptyException {
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
        if(cont1>0 && cont2>0 && cont1<=d2.getDimension() && cont2<=d1.getDimension() && d1.getType()!=d2.getType()){
            List<ResourceType> res1=new ArrayList<>();
            List<ResourceType> res2=new ArrayList<>();
            ResourceType type1=d1.getType();
            ResourceType type2=d2.getType();
            for(int i=0;i<cont1;i++){
                res1.add(d1.removeResource());
            }
            for(int i=0;i<cont2;i++){
                res2.add(d2.removeResource());
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
        //if the second deposit has 0 resource or the second is the same type of the first --> move
        if(cont1>0 && cont2==0 || cont1>0 && cont2>0 && d1.getType()==d2.getType()){
            ResourceType r1=d1.getType();
            d2.addResource(r1);
            d1.removeResource();
        }

    }

    /**
     * discard a pending resource
     * @param res is the type of the resource i want to discard
     * @throws IllegalResourceException when there are no resources of res type in pending
     */
    public void discardResource(ResourceType res) throws IllegalResourceException, DepositableResourceException {
        int n=pendingResources.get(res);
        if(n>0){
            boolean canDiscard=true;
            boolean depositAlreadyExists=false;
            for(Deposit d : maindepot){
                if((d.getType()==res || d.getType()==ResourceType.EMPTY)&& d.freeSpaces()>0 && !depositAlreadyExists){
                    canDiscard=false;
                }
                if(d.getType()==res) depositAlreadyExists=true;
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
    public void addObserver(BoardObserver obs){
        observers.add(obs);
    }

    /**
     * notify the registered obsrvers
     */
    public void notifyObservers(){
        for(BoardObserver o : observers){
            o.updateDiscard(this);
        }
    }

    /**
     * visualize in console the state of the warehouse, useful for debugging
     */
    public void visualize(){
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
    public void replaceWhiteFromPending(ResourceType res,Integer qt) throws NoWhiteResourceException {
        if(pendingResources.containsKey(ResourceType.WHITE) && pendingResources.get(ResourceType.WHITE)>0){
            pendingResources.replace(ResourceType.WHITE,pendingResources.get(ResourceType.WHITE)-1);
            for(int i=0;i<qt;i++)
                pendingResources.replace(res,pendingResources.get(res)+1);
        }else{
            throw new NoWhiteResourceException();
        }
    }

    public Map<ResourceType, Integer> getPendingResources() { return pendingResources; }

    /**
     * count the resource in pending
     * @return the sum of the resource of the map
     */
    public int countPendingResources(){
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
    public ResourceType removeResourceFromDeposit(Integer id) throws DepositNotExistingException, NonEmptyException {
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
