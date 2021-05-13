package it.polimi.ingsw.network;

import it.polimi.ingsw.model.enums.Color;
import it.polimi.ingsw.model.enums.ResourceType;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.exceptions.IllegalCommandException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.messages.commands.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    public static Message parse(String string, Client client) throws IllegalCommandException {
        List<String> resources = Arrays.asList("shield", "coin", "servant", "stone");
        List<String> colors = Stream.of(Color.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());

        string = string.toLowerCase();
        StringTokenizer tokenizer;

        if (string.equals(""))
            throw new IllegalCommandException();

        switch (string) {
            case "help":
                // display available commands
                System.out.println(client.getClientModel().getIdNameLeadersMap());
                break;
            case "players":
                System.out.println(client.getClientModel().getPlayersInOrder().toString().replace("[","").replace("]",""));
                return null;
            case "pass":
                return new PassCommand();
            case "toggle base production":
                return new ToggleProductionCommand(0);
            case "activate productions":
                return new ActivateProductionsCommand();
            case "revert pick up":
                return new RevertPickUpCommand();
            case "display board":
                break;
            case "display faith path":
                break;
            case "display development cards":
                break;
            case "display leader cards":
                break;
            case "display deposits":
                break;
            case "display available development cards":
                break;
            case "display market":
                break;
        }

        if (string.startsWith("register")) {
            tokenizer = new StringTokenizer(string.substring("register".length()));
            try {
                String nickname = tokenizer.nextToken();
                if (!tokenizer.hasMoreElements()) {
                    client.getClientModel().setNickname(nickname);
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
                if (!tokenizer.hasMoreElements() && number >= 1) {
                    return new PlayerNumberResponse(number);
                }
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("choose leaders")) {
            tokenizer = new StringTokenizer(string.substring("choose leaders".length()));
            try {
                int first = Integer.parseInt(tokenizer.nextToken());
                int second = Integer.parseInt(tokenizer.nextToken());
                if (first > 0 && second > 0 && !tokenizer.hasMoreTokens()) {
                    Map<Integer, String> map = client.getClientModel().getIdNameLeadersMap();
                    List<String> listNames = new ArrayList<>();
                    listNames.add(map.get(first));
                    listNames.add(map.get(second));
                    return new ChooseLeadersCommand(listNames);
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("play leader")) {
            tokenizer = new StringTokenizer(string.substring("play leader".length()));
            try {
                int index = Integer.parseInt(tokenizer.nextToken());
                if (index > 0 && !tokenizer.hasMoreTokens())
                    return new PlayLeaderCommand(index - 1);
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("discard leader")) {
            tokenizer = new StringTokenizer(string.substring("discard leader".length()));
            try {
                int index = Integer.parseInt(tokenizer.nextToken());
                if (index > 0 && !tokenizer.hasMoreTokens())
                    return new DiscardLeaderCommand(index - 1);
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("toggle")) {
            tokenizer = new StringTokenizer(string.substring("toggle".length()));
            try {
                String production = tokenizer.nextToken();
                if (production.equals("extra")) {
                    if (tokenizer.nextToken().equals("production")) {
                        int position = Integer.parseInt(tokenizer.nextToken());
                        if (position > 0 && !tokenizer.hasMoreTokens())
                            return new ToggleExtraProductionCommand(position);
                    }
                }
                if (production.equals("production")) {
                    int position = Integer.parseInt(tokenizer.nextToken());
                    if (position > 0 && !tokenizer.hasMoreTokens())
                        return new ToggleProductionCommand(position);
                }
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
                        if (position > 0 && !tokenizer.hasMoreTokens())
                            return new ChooseBonusResourceCommand(ResourceType.valueOfLabel(resource), position);
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("pick up from")) {
            tokenizer = new StringTokenizer(string.substring("pick up from".length()));
            try {
                String source = tokenizer.nextToken();
                if (source.equals("warehouse")) {
                    int position = Integer.parseInt(tokenizer.nextToken());
                    if (position >= 0 && !tokenizer.hasMoreTokens())
                        return new WarehousePickUpCommand(position);
                }
                if (source.equals("strongbox")) {
                    String resource = tokenizer.nextToken();
                    if (resources.contains(resource) && !tokenizer.hasMoreTokens())
                        return new StrongboxPickUpCommand(ResourceType.valueOfLabel(resource));
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
                        int position = Integer.parseInt(tokenizer.nextToken());
                        if (position > 0 && !tokenizer.hasMoreElements())
                            return new DepositResourceCommand(ResourceType.valueOfLabel(resource), position);
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("move from")) {
            tokenizer = new StringTokenizer(string.substring("move from".length()));
            try {
                int source = Integer.parseInt(tokenizer.nextToken());
                if (tokenizer.nextToken().equals("to")) {
                    int destination = Integer.parseInt(tokenizer.nextToken());
                    if (source >= 0 && destination >= 0 && !tokenizer.hasMoreElements())
                        return new MoveResourceCommand(source, destination);
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
                    if (!tokenizer.hasMoreElements())
                        return new DiscardResourceCommand(ResourceType.valueOfLabel(resource));
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
                        if (level >= 0 && tokenizer.nextToken().equals("slot")) {
                            int slot = Integer.parseInt(tokenizer.nextToken());
                            if (!tokenizer.hasMoreElements())
                                return new BuyCardCommand(level - 1, colors.indexOf(color), slot);
                        }
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("toggle discount")) {
            tokenizer = new StringTokenizer(string.substring("toggle discount".length()));
            try {
                String resource = tokenizer.nextToken();
                if (resources.contains(resource) && !tokenizer.hasMoreTokens())
                    return new ToggleDiscountCommand(ResourceType.valueOfLabel(resource));
            } catch (NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("transform white in")) {
            tokenizer = new StringTokenizer(string.substring("transform white in".length()));
            try {
                String resource = tokenizer.nextToken();
                if (resources.contains(resource) && !tokenizer.hasMoreElements())
                    return new TransformWhiteCommand(ResourceType.valueOfLabel(resource));
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("activate market")) {
            tokenizer = new StringTokenizer(string.substring("activate market".length()));
            try {
                String line = tokenizer.nextToken();
                if (line.equals("row") || line.equals("column")) {
                    int position = Integer.parseInt(tokenizer.nextToken());
                    if (position >= 0 && !tokenizer.hasMoreTokens() && position > 0)
                        return new BuyFromMarketCommand(line.charAt(0), position);
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("display opponent board")) {
            tokenizer = new StringTokenizer(string.substring("transform white in".length()));
            try {
                int number = Integer.parseInt(tokenizer.nextToken());
                if (number > 0 && !tokenizer.hasMoreElements());
                // show player's number 'number' board
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("base production")) {
            tokenizer = new StringTokenizer(string.substring("base production".length()));
            try {
                if (tokenizer.nextToken().equals("unknown")) {
                    String option = tokenizer.nextToken();
                    if (option.equals("input") || option.equals("output")) {
                        if (tokenizer.nextToken().equals("to")) {
                            String resource = tokenizer.nextToken();
                            if (resources.contains(resource) && !tokenizer.hasMoreTokens())
                                return new ProductionUnknownCommand(option, ResourceType.valueOfLabel(resource), 0);
                        }
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("production")) {
            tokenizer = new StringTokenizer(string.substring("production".length()));
            try {
                int position = Integer.parseInt(tokenizer.nextToken());
                if (tokenizer.nextToken().equals("unknown")) {
                    String option = tokenizer.nextToken();
                    if (option.equals("input") || option.equals("output")) {
                        if (tokenizer.nextToken().equals("to")) {
                            String resource = tokenizer.nextToken();
                            if (resources.contains(resource) && !tokenizer.hasMoreTokens())
                                return new ProductionUnknownCommand(option, ResourceType.valueOfLabel(resource), position);
                        }
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        if (string.startsWith("extra production")) {
            tokenizer = new StringTokenizer(string.substring("extra production".length()));
            try {
                int position = Integer.parseInt(tokenizer.nextToken());
                if (tokenizer.nextToken().equals("unknown")) {
                    String option = tokenizer.nextToken();
                    if (option.equals("input") || option.equals("output")) {
                        if (tokenizer.nextToken().equals("to")) {
                            String resource = tokenizer.nextToken();
                            if (resources.contains(resource) && !tokenizer.hasMoreTokens())
                                return new ExtraProductionUnknownCommand(option, ResourceType.valueOfLabel(resource), position);
                        }
                    }
                }
            } catch (NumberFormatException | NoSuchElementException e) {
                throw new IllegalCommandException();
            }
        }

        throw new IllegalCommandException();
    }
}
