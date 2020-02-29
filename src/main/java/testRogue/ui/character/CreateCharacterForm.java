package testRogue.ui.character;

import testRogue.json.JsonService;
import testRogue.json.PlayerCharacter;
import ure.areas.UArea;
import ure.commands.UCommand;
import ure.editors.glyphed.GlyphedModal;
import ure.editors.landed.LandedModal;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.HearModal;
import ure.ui.modals.HearModalTitleScreen;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetListVert;

import java.util.List;
import java.util.stream.Collectors;

public class CreateCharacterForm extends UModal {

    WidgetListVert menuWidget;
    UArea area;

    public CreateCharacterForm(int cellwidth, int cellheight, HearModalTitleScreen _callback, String _callbackContext, UArea _area) {
        super(_callback, _callbackContext);


        setDimensions(cellwidth,cellheight);


        setBgColor(new UColor(0.07f,0.07f,0.07f));
        String[] options = new String[]{"Continue", "New World", "Quit"};
        System.out.println("TEST");
        JsonService jsonService = new JsonService();
        List<PlayerCharacter> batye = jsonService.getBatyaObject();
        String[] names = batye.stream().map(x -> x.getName()).toArray(String[]::new);
        menuWidget = new WidgetListVert(this,0,13,names);
        addCenteredWidget(menuWidget);

        commander.showModal(this);
        area = _area;
    }


//    @Override
//    public void hearCommand(UCommand command, GLKey k) {
//
//        if (logoWidget.alpha < 1f) {
//            logoWidget.alpha = 1f;
//            return;
//        }
//        super.hearCommand(command, k);
//    }
//    @Override
//    public void mouseClick() {
//        if (logoWidget.alpha < 1f) {
//            logoWidget.alpha = 1f;
//            return;
//        }
//        super.mouseClick();
//    }
//
//    @Override
//    public void pressWidget(Widget widget) {
//        if (widget == menuWidget) {
//            pickSelection(menuWidget.choice());
//        }
//    }
//    public void hearModalChoices(String context, String option) {
//        if (option.equals("LandEd")) {
//            dismiss();
//            LandedModal modal = new LandedModal(area);
//            commander.showModal(modal);
//        } else if (option.equals("VaultEd")) {
//            commander.launchVaulted();
//        } else if (option.equals("GlyphEd")) {
//            GlyphedModal modal = new GlyphedModal();
//            commander.showModal(modal);
//        }
//    }

    public void hearModalGetString(String context, String input) {
        if (context.equals("name-new-world")) {
            escape();
            ((HearModalTitleScreen) callback).hearModalTitleScreen("New World", input);
        }
    }

    @Override
    public void animationTick() {
//        super.animationTick();
//        area.animationTick();
//        logoWidget.alpha += 0.02f;
//        if (logoWidget.alpha >1f) {
//            logoWidget.alpha = 1f;
//            titleWidget.hidden = false;
//            menuWidget.hidden = false;
//        }
//        fakeTickCount++;
//        if (fakeTickCount > 20) {
//            fakeTickCount = 0;
//            commander.tickTime();
//            commander.letActorsAct();
//        }
    }
}
