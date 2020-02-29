package testRogue.json;

import java.util.ArrayList;
import java.util.List;

public class PlayerCharacter {

    public String name;
    public String description;
    public String bodyType;
    public List<String> moveTypes;
    public String energy;
    public String money;


    public PlayerCharacter() {
        moveTypes = new ArrayList<String>();

        name = "Drunken batya";
        description = "this is drunken batya";
        bodyType = "fat";
        moveTypes.add("perekat");
        energy = "0";
        money = "0";
    }

    public String getName() {
        return name;
    }
}
