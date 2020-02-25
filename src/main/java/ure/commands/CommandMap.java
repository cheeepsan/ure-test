package ure.commands;

import ure.actors.UPlayer;
import ure.ui.modals.UModalMap;

public class CommandMap extends UCommand {

    public static final String id = "MAP";

    public CommandMap() { super(id); }

    @Override
    public void execute(UPlayer player) {
        UModalMap modal = new UModalMap(player.area(), commander.camera().columns-6,  commander.camera().rows-4);
        modal.setTitle(commander.cartographer.describeLabel(player.area().getLabel()));
        commander.showModal(modal);
    }
}
