package testRogue.commander;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import ure.commands.UCommand;
import ure.sys.GLKey;
import ure.sys.Injector;
import ure.sys.UCommander;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TestUCommander extends UCommander {
    private Log log = LogFactory.getLog(TestUCommander.class);
    public TestUCommander() {
        super();
    }

}
