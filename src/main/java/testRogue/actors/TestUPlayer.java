package testRogue.actors;

import ure.actors.UPlayer;
import ure.math.UColor;

public class TestUPlayer extends UPlayer {

    String energy = new String("0");
    String money = new String("0");

    public TestUPlayer(String thename, UColor selfLightColor, int selfLight, int selfLightFalloff,PlayerCharacter character) {
        super(thename, selfLightColor, selfLight, selfLightFalloff);
        this.energy = character.energy;
        this.money = character.money;
    }

    public String getEnergy() {
            return this.energy;
    }

    public String getMoney() {
            return this.money;
    }

}
