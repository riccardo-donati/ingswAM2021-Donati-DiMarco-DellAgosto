package it.polimi.ingsw.network.client.GUI.Controllers;

import it.polimi.ingsw.network.client.GUI.GUI;

public abstract class ControllerGUI {

    protected GUI gui;
    public void setGUI(GUI gui){
        this.gui = gui;
    }
    public void initializeElements(){}
}
