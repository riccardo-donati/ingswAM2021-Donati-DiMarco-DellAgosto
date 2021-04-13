package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.model.exceptions.ResourcesNotAvailableException;
import it.polimi.ingsw.model.exceptions.UnknownFindException;

import java.util.HashMap;
import java.util.Map;

public class Production {
    private Map<ResourceType,Integer> input;
    private Map<ResourceType,Integer> output;
    private boolean selected;

    /**
     * constructor that creates an empty production
     * selected is set to false
     */
    public Production(){
        input=new HashMap<>();
        output=new HashMap<>();
        selected=false;
    }

    /**
     * constructor that creates a production given an input and an output map
     * selected is set to false
     * @param input map that represents the input of the production
     * @param output map that represents the output of the production
     */
    public Production(Map<ResourceType,Integer> input,Map<ResourceType,Integer> output){
        this.input=input;
        this.output=output;
        selected=false;
    }

    /**
     * input get
     * @return the input of the production
     */
    public Map<ResourceType, Integer> getInput() {
        return input;
    }

    /**
     * output get
     * @return the output of the production
     */
    public Map<ResourceType, Integer> getOutput() {
        return output;
    }

    /**
     * selected get
     * @return true if the production is selected
     */
    public boolean checkSelected(){
        return selected;
    }

    /**
     * if the attribute selected is true, it becomes false
     * if the attribute selected is false, it becomes true ONLY if the production is valid (doesn't have UNKNOWN resource type in or out)
     * @throws UnknownFindException if you are trying to select a production not valid
     */
    public void toggleSelected() throws UnknownFindException {
        if(selected)
            selected=false;
        else{
            if(checkValidity()){
                selected=!selected;
            }else throw new UnknownFindException();
        }
    }

    /**
     * adds to the input map a resource type and the corresponding quantity
     * @param resourceType type of input to be added
     * @param quantity quantity of the specified resource
     */
    public void addInput(ResourceType resourceType, Integer quantity) {
        if(input.containsKey(resourceType))
            input.replace(resourceType, input.get(resourceType) + quantity);
        else input.put(resourceType, quantity);
    }

    /**
     * adds to the output map a resource type and the corresponding quantity
     * @param resourceType type of output to be added
     * @param quantity quantity of the specified resource
     */
    public void addOutput(ResourceType resourceType, Integer quantity) {
        if(output.containsKey(resourceType))
            output.replace(resourceType, output.get(resourceType) + quantity);
        else output.put(resourceType, quantity);
    }

    /**
     * removes from the input map the quantity of the resource
     * @param resourceType is the type of the resource
     * @param quantity of the specified resource
     * @throws ResourcesNotAvailableException when the resource is not available
     */
    public void removeInput(ResourceType resourceType, Integer quantity) throws ResourcesNotAvailableException {
        if(input.containsKey(resourceType) && input.get(resourceType)>=quantity){
            input.replace(resourceType,input.get(resourceType)-quantity);
        }else throw new ResourcesNotAvailableException();
    }

    /**
     * removes from the output map the quantity of the resource
     * @param resourceType is the type of the resource
     * @param quantity of the specified resource
     * @throws ResourcesNotAvailableException when the resource is not available
     */
    public void removeOutput(ResourceType resourceType, Integer quantity) throws ResourcesNotAvailableException {
        if(output.containsKey(resourceType) && output.get(resourceType)>=quantity){
            output.replace(resourceType,output.get(resourceType)-quantity);
        }else throw new ResourcesNotAvailableException();
    }

    /**
     * checks whether the production has any UNKNOWN resource type in the input or output map
     * @return false if the production has any UNKNOWN resource type in the input or output map, true otherwise
     */
    public boolean checkValidity(){
        for (Map.Entry<ResourceType, Integer> entry : input.entrySet()) {
            if(entry.getValue()>0 && entry.getKey()==ResourceType.UNKNOWN) return false;
        }
        for (Map.Entry<ResourceType, Integer> entry : output.entrySet()) {
            if(entry.getValue()>0 && entry.getKey()==ResourceType.UNKNOWN) return false;
        }
        return true;
    }
}
