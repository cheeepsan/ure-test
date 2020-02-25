package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.sys.GLKey;
import ure.ui.modals.HearModalDropdown;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalDropdown;

public class WidgetDropdown extends Widget implements HearModalDropdown {
    String[] choices;
    public int selection;

    public WidgetDropdown(UModal modal, int x, int y, String[] choices, int selected) {
        super(modal);
        this.choices = choices;
        selection = selected;
        focusable = true;
        setDimensions(x, y, modal.longestLine(choices), 1);
    }

    @Override
    public void mouseClick(int mousex, int mousey) {
        showDropdown();
    }
    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null)
            if (c.id.equals("PASS"))
                showDropdown();
    }

    @Override
    public void drawMe() {
        drawString(choices[selection], 0, 0, focused ? null : grayColor(), focused ? hiliteColor() : null);
    }

    void showDropdown() {
        UModalDropdown drop = new UModalDropdown(choices, selection, this, "");
        drop.setChildPosition(col, row -selection, modal);
        modal.commander.showModal(drop);
    }

    public void hearModalDropdown(String context, int selection) {
        this.selection = selection;
        modal.widgetChanged(this);
    }

    public String selected() {
        return choices[selection];
    }

    public void selectChoice(String choice) {
        for (int i=0;i<choices.length;i++) {
            if (choice.equals(choices[i])) {
                selection = i;
                return;
            }
        }
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
        if (selection >= choices.length)
            selection = choices.length-1;
    }
}
