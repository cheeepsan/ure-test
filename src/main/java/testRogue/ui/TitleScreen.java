package testRogue.ui;

import testRogue.ui.character.CreateCharacterForm;
import ure.areas.UArea;
import ure.commands.UCommand;
import ure.editors.glyphed.GlyphedModal;
import ure.editors.landed.LandedModal;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.*;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetListVert;
import ure.ui.modals.widgets.WidgetRexImage;
import ure.ui.modals.widgets.WidgetText;

import java.io.File;

public class TitleScreen extends UModal implements HearModalGetString, HearModalChoices {

    WidgetRexImage logoWidget;
    WidgetText titleWidget;
    WidgetListVert menuWidget;

    int fakeTickCount;
    UArea area;

    String titleMsg = "TITLE TEXT TITLE TEXT TITLE TEXT";

    public TitleScreen(int cellwidth, int cellheight, HearModalTitleScreen _callback, String _callbackContext, UArea _area) {
        super(_callback,_callbackContext);

        setDimensions(cellwidth,cellheight);
        escapable = false;
        logoWidget = new WidgetRexImage(this,0,0,"/ure_logo.xp");
        logoWidget.alpha = 0f;
        addCenteredWidget(logoWidget);
        titleWidget = new WidgetText(this,0,11,titleMsg);
        titleWidget.hidden = true;
        addCenteredWidget(titleWidget);

        setBgColor(new UColor(0.07f,0.07f,0.07f));

        String[] options;
        File file = new File(commander.savePath() + "player");
        if (!file.isFile())
            options = new String[]{"New World", "Quit"};
        else
            options = new String[]{"Continue", "New World", "Quit"};
        menuWidget = new WidgetListVert(this,0,13,options);
        menuWidget.hidden = true;
        menuWidget.dismissFlash = true;
        addCenteredWidget(menuWidget);

        fakeTickCount = 0;
        //dismissFrameEnd = 0;
        area = _area;
    }
    @Override
    public void hearCommand(UCommand command, GLKey k) {

        if (logoWidget.alpha < 1f) {
            logoWidget.alpha = 1f;
            return;
        }
        super.hearCommand(command, k);
    }
    @Override
    public void mouseClick() {
        if (logoWidget.alpha < 1f) {
            logoWidget.alpha = 1f;
            return;
        }
        super.mouseClick();
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == menuWidget) {
            pickSelection(menuWidget.choice());
        }
    }

    void pickSelection(String option) {
        if (option.equals("New World")) {
            ((HearModalTitleScreen) callback).hearModalTitleScreen("character", null);
        } else if (option.equals("Credits")) {
            UModalNotify nmodal = new UModalNotify("URE: the unRoguelike Engine\n \nSpunky - meta\nMoycakes - openGL\nKapho - QA, content\nGilmore - misc");
            nmodal.setPad(1, 1);
            nmodal.setTitle("credits");
            commander.showModal(nmodal);
        } else if (option.equals("Edit")) {
            UModalChoices cmodal = new UModalChoices(null, new String[]{"LandEd","VaultEd","GlyphEd"}, this, "edit");
            commander.showModal(cmodal);
        } else {
            dismiss();
            ((HearModalTitleScreen) callback).hearModalTitleScreen(option, null);
        }
    }

    public void hearModalChoices(String context, String option) {
        if (option.equals("LandEd")) {
            dismiss();
            LandedModal modal = new LandedModal(area);
            commander.showModal(modal);
        } else if (option.equals("VaultEd")) {
            commander.launchVaulted();
        } else if (option.equals("GlyphEd")) {
            GlyphedModal modal = new GlyphedModal();
            commander.showModal(modal);
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
        logoWidget.alpha += 0.02f;
        if (logoWidget.alpha >1f) {
            logoWidget.alpha = 1f;
            titleWidget.hidden = false;
            menuWidget.hidden = false;
        }
        fakeTickCount++;
        if (fakeTickCount > 20) {
            fakeTickCount = 0;
            commander.tickTime();
            commander.letActorsAct();
        }
    }
}
