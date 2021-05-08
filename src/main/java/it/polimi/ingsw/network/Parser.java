package it.polimi.ingsw.network;

import it.polimi.ingsw.network.exceptions.IllegalCommandException;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Parser {

    public static void parse(String string) throws IllegalCommandException {
        List<String> resources = Arrays.asList("shield", "coin", "servant", "stone");

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
        }

        if (string.startsWith("play leaders")) {
            tokenizer = new StringTokenizer(string.substring("play leaders".length()));
            try {
                int index = Integer.parseInt(tokenizer.nextToken());

            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("choose leaders")) {
            tokenizer = new StringTokenizer(string.substring("choose leaders".length()));
            try {
                int first = Integer.parseInt(tokenizer.nextToken());
                try {
                    int second = Integer.parseInt(tokenizer.nextToken());
                    if (first < 1 || first > 4 || second < 1 || second > 4 || tokenizer.hasMoreTokens())
                        throw new IllegalCommandException();
                    // do it here
                } catch (NumberFormatException | NoSuchElementException e) {
                    throw new IllegalCommandException();
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("place bonus")) {
            tokenizer = new StringTokenizer(string.substring("place bonus".length()));
            try {
                String resource = tokenizer.nextToken();
                if (!resources.contains(resource))
                    throw new IllegalCommandException();
                try {
                    String in = tokenizer.nextToken();
                    if (!in.equals("in"))
                        throw new IllegalCommandException();
                    int position;
                    try {
                        position = Integer.parseInt(tokenizer.nextToken());
                        if (position < 1 || position > 4 || tokenizer.hasMoreTokens())
                            throw new IllegalCommandException();
                        // --> here <--
                    } catch (NumberFormatException | NoSuchElementException e) {
                        throw new IllegalCommandException();
                    }
                } catch (NoSuchElementException e) {
                    throw new IllegalCommandException();
                }
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if(string.startsWith("pick up from")) {
            tokenizer = new StringTokenizer(string.substring("pick up from".length()));
            try {
                String source = tokenizer.nextToken();
                if (source.equals("warehouse")) {
                    int position;
                    try {
                        position = Integer.parseInt(tokenizer.nextToken());
                    } catch (NumberFormatException | NoSuchElementException e) {
                        throw new IllegalCommandException();
                    }
                    if (position < 0 || tokenizer.hasMoreTokens())
                        throw new IllegalCommandException();
                    // do it here
                }
                if (source.equals("strongbox")) {
                    String resource;
                    try {
                        resource = tokenizer.nextToken();
                    } catch (NoSuchElementException e) {
                        throw new IllegalCommandException();
                    }
                    if (!resources.contains(resource) || tokenizer.hasMoreTokens())
                        throw new IllegalCommandException();
                    // do it here
                }
                throw new IllegalCommandException();
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if(string.startsWith("activate market")) {
            tokenizer = new StringTokenizer(string.substring("activate market".length()));
            try {
                String line = tokenizer.nextToken();
                if (!(line.equals("row") || line.equals("column")))
                    throw new IllegalCommandException();
                try {
                    int position;
                    try {
                        position = Integer.parseInt(tokenizer.nextToken());
                    } catch (NumberFormatException e) {
                        throw new IllegalCommandException();
                    }
                    if (position < 1 || (line.equals("row") && position > 3) || (line.equals("column") && position > 4))
                        throw new IllegalCommandException();
                    if (tokenizer.hasMoreTokens())
                        throw new IllegalCommandException();
                    //return new activateMarketMessage(line.charAt(0), position);
                } catch (NoSuchElementException e) {
                    throw new IllegalCommandException();
                }
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        // throw new InvalidCommandException();
    }
}
