package it.polimi.ingsw.model;

import it.polimi.ingsw.model.interfaces.Requirement;
import java.util.ArrayList;
import java.util.List;

public class LeaderCard {

    private List<Requirement> requirements = new ArrayList<>();
    private List<SpecialAbility> specialAbilities = new ArrayList<>();
    private Integer points;

    /**
     * requirements list getter
     * @return list of requirements
     */
    public List<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * special abilities list getter
     * @return list of special abilities
     */
    public List<SpecialAbility> getSpecialAbilities() {
        return specialAbilities;
    }

    /**
     * getter of the relative leader card's points
     * @return returns leader card's victory points
     */
    public Integer getPoints(){
        return points;
    }

    /**
     * adds a requirement to the list of requirements
     * @param requirement requirement to add
     */
    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }

    /**
     * adds a special ability to the list of special abilities
     * @param specialAbility ability to add
     */
    public void addSpecialAbility(SpecialAbility specialAbility) {
        this.specialAbilities.add(specialAbility);
    }
}
