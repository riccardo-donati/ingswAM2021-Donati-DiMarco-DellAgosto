package it.polimi.ingsw.network;

import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.messages.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Parser {

    public static Message parse(String string) throws IllegalCommandException {
        List<String> resources = Arrays.asList("shield", "coin", "servant", "stone");
        List<String> colors = Arrays.asList("green", "purple", "yellow", "blue");

        string = string.toLowerCase();
        StringTokenizer tokenizer;

        if (string.equals(""))
            throw new IllegalCommandException();

        switch (string) {
            case "help":
                // display available commands
                break;
            case "players":
                // display players list
                break;
            case "pass":
                //return new passMessage();
                break;
            case "toggle base production":
                //message select base production
                break;
            case "activate productions":
                break;
            case "toggle discount":
                break;
            case "revert pickup":
                break;
        }

        if (string.startsWith("register")) {
            tokenizer = new StringTokenizer(string.substring("register".length()));
            try {
                String nickname = tokenizer.nextToken();
                if (!tokenizer.hasMoreElements()) {
                    return new RegisterResponse(nickname);
                }
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("numberofplayers")) {
            tokenizer = new StringTokenizer(string.substring("numberofplayers".length()));
            try {
                int number = Integer.parseInt(tokenizer.nextToken());
                if (!tokenizer.hasMoreElements()) {
                    return new PlayerNumberResponse(number);
                }
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("buy")) {
            tokenizer = new StringTokenizer(string.substring("buy".length()));
            try {
                String color = tokenizer.nextToken();
                if (colors.contains(color)) {
                    if (tokenizer.nextToken().equals("card") && tokenizer.nextToken().equals("level")) {
                        int level = Integer.parseInt(tokenizer.nextToken());
                        if (tokenizer.nextToken().equals("slot")) {
                            int slot = Integer.parseInt(tokenizer.nextToken());
                            if (tokenizer.hasMoreElements());
                            // buy 'color' card level 'level' and place in slot 'slot'
                        }
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("discard")) {
            tokenizer = new StringTokenizer(string.substring("discard".length()));
            try {
                String resource = tokenizer.nextToken();
                if (resources.contains(resource)) {
                    if (!tokenizer.hasMoreElements());
                        // discard 'resource'
                }
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("move from")) {
            tokenizer = new StringTokenizer(string.substring("move from".length()));
            try {
                int source = Integer.parseInt(tokenizer.nextToken());
                if (tokenizer.nextToken().equals("to")) {
                    int destination = Integer.parseInt(tokenizer.nextToken());
                    if (source >= 0 && destination >= 0 && !tokenizer.hasMoreElements());
                        // move resource from 'source' to 'destination'
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("deposit")) {
            tokenizer = new StringTokenizer(string.substring("deposit".length()));
            try {
                String resource = tokenizer.nextToken();
                if (resources.contains(resource)) {
                    if (tokenizer.nextToken().equals("in")) {
                        if (tokenizer.nextToken().equals("warehouse")) {
                            int position = Integer.parseInt(tokenizer.nextToken());
                            if (position > 0 && !tokenizer.hasMoreElements());
                                //here deposit 'resource' in deposit number 'position'
                        }
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("toggle")) {
            tokenizer = new StringTokenizer(string.substring("toggle".length()));
            try {
                String production = tokenizer.nextToken();
                if (production.equals("base")) {
                    if (tokenizer.nextToken().equals("production")) {
                        if (!tokenizer.hasMoreTokens());
                            //here toggle base production
                    }
                }
                if (production.equals("extra")) {
                    if (tokenizer.nextToken().equals("production")) {
                        int position = Integer.parseInt(tokenizer.nextToken());
                        if (position > 0 && !tokenizer.hasMoreTokens());
                            //here toggle extra production in position 'position'
                    }
                }
                if (production.equals("production")) {
                    int position = Integer.parseInt(tokenizer.nextToken());
                    if (position > 0 && !tokenizer.hasMoreTokens());
                        //here toggle production in position 'position'
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("play leader")) {
            tokenizer = new StringTokenizer(string.substring("play leader".length()));
            try {
                int index = Integer.parseInt(tokenizer.nextToken());
                if (index > 0 && tokenizer.hasMoreTokens());
                    //here play leader in position 'index'
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("discard leader")) {
            tokenizer = new StringTokenizer(string.substring("discard leader".length()));
            try {
                int index = Integer.parseInt(tokenizer.nextToken());
                if (index > 0 && tokenizer.hasMoreTokens());
                    //here
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("choose leaders")) {
            tokenizer = new StringTokenizer(string.substring("choose leaders".length()));
            try {
                int first = Integer.parseInt(tokenizer.nextToken());
                int second = Integer.parseInt(tokenizer.nextToken());
                if (!tokenizer.hasMoreTokens());
                    // do it here
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("place bonus")) {
            tokenizer = new StringTokenizer(string.substring("place bonus".length()));
            try {
                String resource = tokenizer.nextToken();
                if (resources.contains(resource)) {
                    String in = tokenizer.nextToken();
                    if (in.equals("in")) {
                        int position = Integer.parseInt(tokenizer.nextToken());
                        if (position > 0 && !tokenizer.hasMoreTokens());
                            // place 'resource' in deposit 'position'
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if(string.startsWith("pick up from")) {
            tokenizer = new StringTokenizer(string.substring("pick up from".length()));
            try {
                String source = tokenizer.nextToken();
                if (source.equals("warehouse")) {
                    int position = Integer.parseInt(tokenizer.nextToken());
                    if (position < 0 || tokenizer.hasMoreTokens())
                        throw new IllegalCommandException();
                    // do it here
                }
                if (source.equals("strongbox")) {
                    String resource = tokenizer.nextToken();
                    if (resources.contains(resource) && !tokenizer.hasMoreTokens());
                        // do it here
                }
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if(string.startsWith("activate market")) {
            tokenizer = new StringTokenizer(string.substring("activate market".length()));
            try {
                String line = tokenizer.nextToken();
                if (line.equals("row") || line.equals("column")) {
                    int position = Integer.parseInt(tokenizer.nextToken());
                    if (!tokenizer.hasMoreTokens());
                        //return new activateMarketMessage(line.charAt(0), position);
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        throw new IllegalCommandException();
    }
}
