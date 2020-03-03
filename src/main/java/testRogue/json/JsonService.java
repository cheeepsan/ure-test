package testRogue.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import testRogue.actors.PlayerCharacter;
import testRogue.things.items.ShopThing;
import ure.sys.Injector;
import ure.sys.ResourceManager;
import ure.sys.UConfig;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonService {

    @Inject
    UConfig config;
    @Inject
    ResourceManager resourceManager;

    protected String batyaPath;
    protected String shopThingsPath;
    protected String categoryPath;

    public JsonService() {
        this.batyaPath = "testRogue/characters/batyaList.json";
        this.shopThingsPath = "testRogue/items/shopThings.json";
        Injector.getAppComponent().inject(this);
    }

    public List<PlayerCharacter> getBatyaObject() {
        ObjectMapper objectMapper = new ObjectMapper();
        PlayerCharacter[] batya = {};
        List<PlayerCharacter> batyaList = new ArrayList<>();
        try {
            String json = getJsonValueFromResourceFile(batyaPath);
            batya = objectMapper.readValue(json, PlayerCharacter[].class);
            batyaList = Arrays.asList(batya);
        } catch (Exception e) {
            System.out.println("exception in test/jsonService" + e);
            PlayerCharacter lonelyBatya = new PlayerCharacter(); // default batya
            batyaList.add(lonelyBatya);
        }

        return batyaList;
    }

    public List<ShopThing> getItemList() {
        ObjectMapper objectMapper = new ObjectMapper();
        ShopThing[] itemArray = {};
        List<ShopThing> itemList = new ArrayList<>();
        try {
            String json = getJsonValueFromResourceFile(shopThingsPath);
            itemArray = objectMapper.readValue(json, ShopThing[].class);
            itemList = Arrays.asList(itemArray);
        } catch (Exception e) {
            System.out.println("exception in test/jsonService" + e);
        }

        return itemList;
    }

    public String getJsonValueFromResourceFile(String path ) {
        String jsonText = null;
        try {

            File file = new File(config.getResourcePath() + path);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, "UTF-8");
            jsonText = str;

        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return jsonText;
    }

    public String getBatyaList() {
        return this.getJsonValueFromResourceFile(this.batyaPath);
    }
}
