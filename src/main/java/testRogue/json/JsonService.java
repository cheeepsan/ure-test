package testRogue.json;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import play.api.libs.json.JsObject;
import play.api.libs.json.JsValue;
import play.api.libs.json.Json;
import ure.sys.Injector;
import ure.sys.ResourceManager;
import ure.sys.UConfig;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonService {

    @Inject
    UConfig config;
    @Inject
    ResourceManager resourceManager;

    protected String batyaPath; // батя вряд ли будет такого размере, что придется читать по лайнам

    public JsonService() {
        this.batyaPath = "testRogue/batyaList.json";
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
