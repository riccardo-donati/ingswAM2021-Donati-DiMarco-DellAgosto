package it.polimi.ingsw.network;

import it.polimi.ingsw.network.client.CLI;
import it.polimi.ingsw.network.client.GUI.GUI;
import it.polimi.ingsw.network.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MastersOfRenaissance {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int userChoice;
        int counter = 0;
        final int maxAttempts = 3;
        System.out.println(Utilities.MORTitle());
        System.out.println("Welcome to Masters of Renaissance!\n");
        System.out.println("What application would you like to launch?");
        System.out.println("1: Server");
        System.out.println("2: CLI Client");
        System.out.println("3: GUI Client");

        do {
            try {
                System.out.print(">");
                userChoice = Integer.parseInt(in.readLine());
                switch (userChoice) {
                    case 1 -> Server.main(args);
                    case 2 -> CLI.main(null);
                    case 3 -> GUI.main(null);
                    default -> throw new IllegalArgumentException();
                }
            } catch (IOException e) {
                System.err.println("Error, closing . . .");
                return;
            } catch (NumberFormatException e) {
                System.err.println("Please insert a number");
                counter++;
            } catch (IllegalArgumentException e) {
                counter++;
                if (counter < maxAttempts)
                    System.err.println("Please Insert a valid option");
                else System.err.println("You have exceeded the maximum number of attempts, closing . . .");
            }
        } while (counter < maxAttempts);
    }
}
