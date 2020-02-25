package ure.commands;

import ure.actors.UPlayer;
import ure.terrain.Stairs;
import ure.terrain.UTerrain;

public class CommandTravel extends UCommand {

    public static final String id = "TRAVEL";

    public CommandTravel() {
        super(id);
    }
    @Override
    public void execute(UPlayer player) {
        UTerrain t = player.area().terrainAt(player.areaX(), player.areaY());
        if (t instanceof Stairs) {
            ((Stairs)t).transportActor(player);
        } else {
            commander.printScroll("You don't see anything to move through.");
        }
    }
}
