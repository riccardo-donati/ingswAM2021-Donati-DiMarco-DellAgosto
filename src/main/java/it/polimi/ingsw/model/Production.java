package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class Production {
    private Map<ResourceType,Integer> input;
    private Map<ResourceType,Integer> output;
    private boolean selected;

    public Production(){
        input=new HashMap<>();
        output=new HashMap<>();
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
     * if production is selected, deselect it and vice versa
     */
    public void toggleSelected(){
        selected=!selected;
    }

    /**
     * input set
     * @param input
     */
    public void setInput(Map<ResourceType, Integer> input) {
        this.input = input;
    }

    /**
     * output set
     * @param output
     */
    public void setOutput(Map<ResourceType, Integer> output) {
        this.output = output;
    }
}
