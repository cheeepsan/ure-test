package testRogue.ui.character;

import testRogue.ExampleGame;
import testRogue.json.JsonService;
import testRogue.actors.PlayerCharacter;
import ure.areas.UArea;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.*;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetListVert;

import java.util.List;
import java.util.stream.Stream;

public class CreateCharacterForm extends UModal {

    WidgetListVert menuWidget;
    UArea area;
    List<PlayerCharacter> batye = null;
    ExampleGame game;
    int fakeTickCount;

    public CreateCharacterForm(int cellwidth, int cellheight, HearModalTitleScreen _callback, String _callbackContext, UArea _area, ExampleGame game) {
        super(_callback, _callbackContext);

        fakeTickCount = 0;

        setDimensions(cellwidth,cellheight);


        setBgColor(new UColor(0.07f,0.07f,0.07f));
        System.out.println("TEST");
        JsonService jsonService = new JsonService();
        batye = jsonService.getBatyaObject();
        Stream<String> names = batye.stream().map(x -> x.getName());
        String[] options = Stream.concat(names, Stream.of("Back")).toArray(String[]::new);

        menuWidget = new WidgetListVert(this,0,13, options);
        addCenteredWidget(menuWidget);

        commander.showModal(this);
        area = _area;

        this.game = game;
    }


    @Override
    public void hearCommand(UCommand command, GLKey k) {

//        if (logoWidget.alpha < 1f) {
//            logoWidget.alpha = 1f;
//            return;
//        }
        super.hearCommand(command, k);
    }
    @Override
    public void mouseClick() {
//        if (logoWidget.alpha < 1f) {
//            logoWidget.alpha = 1f;
//            return;
//        }
        super.mouseClick();
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == menuWidget) {
            pickSelection(menuWidget.choice());
        }
    }


    void pickSelection(String option) {
        if (option.equals("Back")) {
            ((HearModalTitleScreen) callback).hearModalTitleScreen("main menu", null);
//            UModalGetString smodal = new UModalGetString("Name your character:", 15, 25,this, "name-new-world");
//            commander.showModal(smodal);

        } else {
            ((HearModalTitleScreen) callback).hearModalTitleScreen("start", null);
            PlayerCharacter selectedPlayer = this.batye.stream().filter(c -> c.getName().equals(option)).findFirst().get();
            this.game.startGame(selectedPlayer.name, selectedPlayer);
            dismiss();
        }
    }

    public void hearModalGetString(String context, String input) {
        if (context.equals("name-new-world")) {
            escape();
            ((HearModalTitleScreen) callback).hearModalTitleScreen("New World", input);
        }
    }

    @Override
    public void animationTick() {
        super.animationTick();
        area.animationTick();
        fakeTickCount++;
        if (fakeTickCount > 20) {
            fakeTickCount = 0;
            commander.tickTime();
            commander.letActorsAct();
        }
    }
}
