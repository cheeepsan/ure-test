package testRogue.things;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import testRogue.things.items.ShopThing;
import ure.sys.Injector;
import ure.things.Pile;
import ure.things.UThing;
import ure.things.UThingCzar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TestUThingCzar extends UThingCzar {

//    private HashMap<String, UThing> thingsByName;


    private HashMap<Integer, UThing> shopThingsById = new HashMap<Integer, UThing>();

    private Log log = LogFactory.getLog(UThingCzar.class);

    public TestUThingCzar() {
        Injector.getAppComponent().inject(this);
    }

    public void loadThings() {
        thingsByName = new HashMap<>();
        for (String resource : resourceManager.getResourceFiles("/things")) {
            if (resource.endsWith(".json")) {
                try {
                    InputStream inputStream = resourceManager.getResourceAsStream("/things/" + resource);
                    UThing[] thingObjs = objectMapper.readValue(inputStream, UThing[].class);
                    for (UThing thing : thingObjs) {
                        thing.initializeAsTemplate();
                        thingsByName.put(thing.getName(), thing);
                        log.debug("loaded " + thing.getName());
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }

        this.loadShopUThings();
        log.info("UthingsCzar done");
    }
    @Override
    public UThing getThingByName(String name) {
        UThing template = thingsByName.get(name);
        UThing clone = template.makeClone();
        clone.initializeAsCloneFrom(template);
        clone.setID(super.commander.generateNewID(clone));
        return clone;
    }

    public String[] getThingsByTag(String tag, int level) {
        ArrayList<UThing> things = new ArrayList<>();
        for (String actorname : thingsByName.keySet()) {
            UThing thing = thingsByName.get(actorname);
            if (thing.isTagAndLevel(tag, level)) {
                things.add(thing);
            }
        }
        String[] names = new String[things.size()];
        int i = 0;
        for (UThing thing: things) {
            names[i] = thing.getName();
            i++;
        }
        return names;
    }

    public UThing getPile(String name, int count) {
        UThing pile = getThingByName(name);
        if (pile != null) {
            ((Pile)pile).setCount(count);
        }
        return pile;
    }

    public void loadShopUThings() {
        for(ShopThing item: jsonService.getItemList()) {
            this.shopThingsById.put(item.id, item);
        }
    }

    public Set<String> getAllThings() { return thingsByName.keySet(); }
}
