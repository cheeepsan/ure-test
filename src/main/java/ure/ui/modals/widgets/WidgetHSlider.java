package ure.ui.modals.widgets;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.UModal;

public class WidgetHSlider extends Widget {

    public int value, valuemin, valuemax;
    int length;
    boolean showNumber;
    String label;
    public UColor color;

    public WidgetHSlider(UModal modal, int x, int y, String label, int length, int value, int valuemin, int valuemax, boolean showNumber) {
        super(modal);
        this.label = label;
        setDimensions(x,y,length + (showNumber ? 3 : 0) + modal.textWidth(label),1);
        focusable = true;
        this.value = value;
        this.valuemin = valuemin;
        this.valuemax = valuemax;
        this.length = length;
        this.showNumber = showNumber;
        color = modal.config.getHiliteColor();
    }

    @Override
    public void drawMe() {
        modal.renderer.drawRectBorder(0, 0, length*gw(),gh(),focused ? 2 : 1, UColor.BLACK, modal.config.getHiliteColor());
        modal.renderer.drawRect(0, 0, (int)((length*gw()) * (float)(value-valuemin)/(float)(valuemax-valuemin)), gh(), color);
        if (showNumber)
            modal.drawString(Integer.toString(value), length + 1, 0, null, focused ? modal.config.getHiliteColor() : null);
        modal.drawString(label, length + 3, 0, focused ? null : grayColor());
    }

    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null) {
            if (c.id.equals("MOVE_W")) setValue(Math.max(valuemin, value-1));
            else if (c.id.equals("LATCH_W")) setValue(Math.max(valuemin,value-10));
            else if (c.id.equals("MOVE_S")) setValue(Math.max(valuemin,value-100));
            else if (c.id.equals("MOVE_E")) setValue(Math.min(valuemax, value+1));
            else if (c.id.equals("LATCH_E")) setValue(Math.min(valuemax, value+10));
            else if (c.id.equals("MOVE_N")) setValue(Math.min(valuemax, value+100));
        }
    }

    @Override
    public void mouseClick(int mousex, int mousey) {
        float frac = (float)mousePixelX() / (float)(length * gw());
        frac = Math.max(0f, Math.min(1f, frac));
        setValue(valuemin + (int)((float)(valuemax - valuemin) * frac));
    }

    void setValue(int v) {
        value = v;
        modal.widgetChanged(this);
    }
}