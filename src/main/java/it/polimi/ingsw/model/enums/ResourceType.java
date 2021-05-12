package it.polimi.ingsw.model.enums;

public enum ResourceType {
    YELLOW("coin"),
    GREY("stone"),
    VIOLET("servant"),
    BLUE("shield"),
    RED(""),
    WHITE(""),
    UNKNOWN(""),
    EMPTY("");

    public String label;

    ResourceType(String type) {
        label = type;
    }

    public static ResourceType valueOfLabel(String label) {
        for (ResourceType resourceType : values()) {
            if (resourceType.label.equals(label)) {
                return resourceType;
            }
        }
        return null;
    }

}
