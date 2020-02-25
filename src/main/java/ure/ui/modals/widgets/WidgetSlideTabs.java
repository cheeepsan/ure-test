package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.sys.GLKey;
import ure.ui.modals.UModal;

import java.util.ArrayList;

public class WidgetSlideTabs extends Widget {

    public ArrayList<String> tabs;
    int[] offsets;
    public int selection;
    int pixelpos;
    int targetpos;
    String separator = " | ";

    public WidgetSlideTabs(UModal modal, int x, int y, int width, ArrayList<String> tabs, int selected) {
        super(modal);
        setTabs(tabs);
        focusable = true;
        setDimensions(x,y,width,1);
        setClipsToBounds(true);
        select(selected);
    }

    public void setTabs(ArrayList<String> newtabs) {
        tabs = newtabs;
        offsets = new int[tabs.size()];
        int offmax = 0;
        for (int i=0;i<tabs.size();i++) {
            offsets[i] = offmax;
            offmax += modal.renderer.textWidth(tabs.get(i) + separator);
        }
    }

    public void select(int newselection) {
        selection = newselection;
        targetpos = ((cellw *gw() - modal.renderer.textWidth(tabs.get(selection))) / 2) - offsets[selection];
        modal.widgetChanged(this);
    }

    @Override
    public void drawMe() {
        for (int i=0;i<tabs.size();i++) {
            if (focused && i == selection)
                modal.renderer.drawRect(pixelpos + offsets[i], 0, modal.renderer.textWidth(tabs.get(i)), gh(), hiliteColor());
            modal.renderer.drawString(pixelpos + offsets[i], 0, (i == selection) ? modal.config.getTextColor() : grayColor(), tabs.get(i));

        }
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_W")) {
                select(cursorMove(selection, -1, tabs.size()));
            } else if (c.id.equals("MOVE_E")) {
                select(cursorMove(selection, 1, tabs.size()));
            }
        }
    }

    @Override
    public void mouseClick(int mousex, int mousey) {
        int clicked = tabs.size()-1;
        int barx = mousePixelX() - pixelpos;
        for (int i=1;i<tabs.size();i++) {
            if (barx >= offsets[i-1]) {
                clicked = i-1;
            }
        }
        if (barx < 0) clicked = 0;
        if (barx >= offsets[tabs.size()-1]) clicked = tabs.size()-1;
        select(clicked);
    }

    @Override
    public void animationTick() {
        if (pixelpos != targetpos) {
            int diff = targetpos - pixelpos;
            pixelpos += (diff / 4);
        }
    }
}
