package testRogue.map.scaper;

import ure.areas.UArea;
import ure.areas.gen.ULandscaper;

public class TestZavodScaper extends ULandscaper {

    public static final String TYPE = "zavodscaper";

    public TestZavodScaper() {
        super(TYPE);
    }

    public void buildArea(UArea area, int level, String[] tags) {
        fillRect(area, "wall", 0, 0, area.xsize - 1, area.ysize - 1);
        fillRect(area, "floor", 1, 1, area.xsize - 2, area.ysize - 2);

        scatterThings(area, new String[]{"lamppost", "crystal stalagmite"}, new String[]{"floor"}, 15);
    }
}
