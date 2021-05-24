package it.polimi.ingsw.network.client.GUI.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LogIn extends ControllerGUI{
    private static final String NPLAYERS = "numberOfPlayers.fxml";

    public LogIn(){
    }

    @FXML private TextField username;
    @FXML private TextField ipAddress;
    @FXML private TextField portNumber;
    @FXML private Label emptyInfo;

    public void userLogIn(ActionEvent event)throws Exception{
            try {
                Socket socket = new Socket(ipAddress.getText(), Integer.parseInt(portNumber.getText()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner in = new Scanner(socket.getInputStream());
            } catch (Exception e){
                System.out.println("Server not available");
                return;
            }
            gui.changeScene(NPLAYERS);
        if(username.getText().isEmpty() || ipAddress.getText().isEmpty() || portNumber.getText().isEmpty()){
            emptyInfo.setText("Empty info, try Again");
        }
        else{
            emptyInfo.setText("Wrong info, try Again");
        }
    }

    public void quit(ActionEvent event){
        System.exit(0);
    }

}
