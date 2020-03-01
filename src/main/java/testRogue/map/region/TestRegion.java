package testRogue.map.region;

import ure.areas.URegion;
import ure.areas.gen.ULandscaper;

public class TestRegion extends URegion {

    public TestRegion(String _id, String _name, ULandscaper[] _landscapers, String[] _tags, int _xsize, int _ysize,
                               int _maxlevel, String _inwardExitType, String _outwardExitType, String _bgm) {
        super(_id,_name,_landscapers,_tags,_xsize,_ysize,_maxlevel,_inwardExitType,_outwardExitType,_bgm);
    }

    @Override
    public String describeLabel(String label, String labelname, int labeldata) {
        return getName();
    }
}
