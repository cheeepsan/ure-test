package ure.editors.landed;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Metascaper;
import ure.areas.gen.ULandscaper;
import ure.areas.gen.shapers.*;
import ure.math.UColor;
import ure.ui.ULight;
import ure.ui.modals.UModalLoading;
import ure.ui.modals.UModalTabs;
import ure.ui.modals.widgets.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LandedModal extends UModalTabs {

    UArea area;
    Metascaper scaper;

    String[] shaperNames;

    ArrayList<Layer> layers;
    ArrayList<HashMap<String,Shaper>> layerShapers;
    HashMap<String,Class> shaperClasses;
    int layerIndex;
    Layer layer;

    ArrayList<ULight> roomLights;

    WidgetDropdown vaultSetPicker;

    WidgetButton layerUpButton, layerDownButton, layerDeleteButton;
    WidgetDropdown layerPicker;
    HashMap<String,Widget> shaperWidgets;

    WidgetHSlider densitySlider;
    WidgetRadio pruneRadio, wipeRadio, roundRadio, invertRadio;
    WidgetDropdown shaperPicker;

    WidgetHSlider areaWidthSlider, areaHeightSlider;
    WidgetTerrainpick fillPicker, terrainPicker, doorPicker, entrancePicker, exitPicker;
    WidgetHSlider doorSlider, exitDistanceSlider;
    WidgetDropdown drawPicker;



    WidgetHSlider lightChanceSlider;
    WidgetButton lightNewAmbient, lightNewPoint;
    WidgetListVert lightList;

    WidgetSlideTabs tabSlider;
    WidgetButton regenButton;
    WidgetRadio autoRegenRadio;
    WidgetButton quitButton;

    boolean dragging = false;
    int dragStartX, dragStartY;
    int dragCenterX, dragCenterY;

    public LandedModal(UArea area) {
        super(null, "");
        this.area = area;

        layers = new ArrayList<>();
        shaperClasses = new HashMap<>();
        shaperClasses.put("Caves", Caves.class);
        shaperClasses.put("Mines", Mines.class);
        shaperClasses.put("Growdungeon", Growdungeon.class);
        shaperClasses.put("Chambers", Chambers.class);
        shaperClasses.put("Ruins", Ruins.class);
        shaperClasses.put("Convochain", Convochain.class);
        shaperClasses.put("Blobs", Blobs.class);
        shaperClasses.put("Roads", Roads.class);
        shaperClasses.put("Outline", Outline.class);
        shaperClasses.put("Connector", Connector.class);
        shaperNames = new String[]{
                "Caves",
                "Mines",
                "Growdungeon",
                "Chambers",
                "Ruins",
                "Convochain",
                "Blobs",
                "Roads",
                "Outline",
                "Connector"
        };
        layerShapers = new ArrayList<>();
        shaperWidgets = new HashMap<>();

        regenButton = new WidgetButton(this, 0, 34, "[ Regenerate ]", null);
        addWidget(regenButton);
        autoRegenRadio = new WidgetRadio(this, 7, 34, "auto", null, null, true);
        addWidget(autoRegenRadio);
        quitButton = new WidgetButton(this, 16, 34, "[ Quit ]", null);
        addWidget(quitButton);


        changeTab("Global");

        fillPicker = new WidgetTerrainpick(this, 0, 0, "fill:", "rock");
        addWidget(fillPicker);
        areaWidthSlider = new WidgetHSlider(this, 0, 2, "width", 6, 100, 40, 200, true);
        areaHeightSlider = new WidgetHSlider(this, 0, 3, "height", 6, 100, 40, 200, true);
        addWidget(areaWidthSlider);
        addWidget(areaHeightSlider);

        changeTab("Layers");
        layerPicker = new WidgetDropdown(this, 0, 0, new String[]{"<new layer>"}, 0);
        addWidget(layerPicker);
        layerUpButton = new WidgetButton(this, 16, 1, "[ Up ]", null);
        layerDownButton = new WidgetButton(this,16,0, "[ Down ]", null);
        layerDeleteButton = new WidgetButton(this, 16, 3, "[ Delete ]", null);
        addWidget(layerUpButton);
        addWidget(layerDownButton);
        addWidget(layerDeleteButton);

        shaperPicker = new WidgetDropdown(this, 9, 0, shaperNames, 0);
        addWidget(shaperPicker);
        terrainPicker = new WidgetTerrainpick(this, 0, 2, "terrain:", "floor");
        addWidget(terrainPicker);
        addWidget(new WidgetText(this, 0, 3, "Draw:"));
        drawPicker = new WidgetDropdown(this, 5, 3, new String[]{"All", "In blocked only", "In unblocked only"}, 0);
        addWidget(drawPicker);

        makeShaperWidgets();

        densitySlider = new WidgetHSlider(this, 0, 27, "density", 6, 100, 0, 100, true);
        pruneRadio = new WidgetRadio(this, 0, 29, "prune dead ends", null, null, true);
        wipeRadio = new WidgetRadio(this,0,30,"wipe small regions", null, null, true);
        roundRadio = new WidgetRadio(this, 0, 31, "round corners", null, null, false);
        invertRadio = new WidgetRadio(this, 0, 32, "invert", null, null, false);
        addWidget(densitySlider);
        addWidget(pruneRadio);
        addWidget(wipeRadio);
        addWidget(roundRadio);
        addWidget(invertRadio);


        changeTab("Decorate");

        doorSlider = new WidgetHSlider(this,  0, 0,"door chance", 6, 0, 0, 100, true);
        addWidget(doorSlider);
        doorPicker = new WidgetTerrainpick(this, 0, 2, "door type:", "door");
        addWidget(doorPicker);

        lightChanceSlider = new WidgetHSlider(this, 0, 4, "room light chance", 6, 0, 0, 100, true);
        addWidget(lightChanceSlider);
        lightNewAmbient = new WidgetButton(this, 0, 6, "[ New Ambient ]", null);
        addWidget(lightNewAmbient);
        lightNewPoint = new WidgetButton(this, 10, 6, "[ New Point ]", null);
        addWidget(lightNewPoint);
        lightList = new WidgetListVert(this, 0, 7, new String[]{});
        addWidget(lightList);

        addWidget(new WidgetText(this, 0, 12, "vault set:"));
        vaultSetPicker = new WidgetDropdown(this, 6, 12, commander.getResourceList("vaults"), 0);
        addWidget(vaultSetPicker);


        changeTab("Stairs");

        entrancePicker = new WidgetTerrainpick(this, 0, 0, "entrance type:", "null");
        addWidget(entrancePicker);
        exitPicker = new WidgetTerrainpick(this,0,2,"exit type:", "null");
        addWidget(exitPicker);
        exitDistanceSlider = new WidgetHSlider(this,0,6,"exit min distance:", 6, 30, 1, 100, true);
        addWidget(exitDistanceSlider);

        changeTab(null);

        tabSlider = new WidgetSlideTabs(this, 0, 36, 20, tabList(), 0);
        addWidget(tabSlider);

        sizeToWidgets();

        escapable = false;
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());

        //scaper = ((Metascaper)(loadScaper("testscaper")));
        scaper = new Metascaper();
        roomLights = new ArrayList<>();

        changeTab("Layers");
        makeNewLayer();
        changeTab("Global");
    }

    ULandscaper loadScaper(String filename) {
        String path = commander.savePath();
        File file = new File(path + filename);
        try (
                FileInputStream stream = new FileInputStream(file);
        ) {
            Metascaper scaper = (Metascaper)(objectMapper.readValue(stream, ULandscaper.class));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        layers = scaper.layers;
        layerIndex = 0;
        layer = layers.get(0);

        return null;
    }

    void saveScaper(ULandscaper scaper, String filename) {
        String path = commander.savePath();
        File file = new File(path + filename);
        try (
                FileOutputStream stream = new FileOutputStream(file);
        ) {
            JsonFactory jfactory = new JsonFactory();
            JsonGenerator jGenerator = jfactory
                    .createGenerator(stream, JsonEncoding.UTF8);
            jGenerator.setCodec(objectMapper);
            jGenerator.writeObject(scaper);
            jGenerator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void removeShaperWidgets() {
        for (Widget w : shaperWidgets.values())
            removeWidget(w);
        shaperWidgets.clear();
    }
    void makeShaperWidgets() {
        if (layer == null) return;
        if (layer.shaper == null) return;
        int y = 6;
        ArrayList<String> ni = new ArrayList<>();
        for (String pi : layer.shaper.paramsI.keySet())
            ni.add(pi);
        Collections.sort(ni);
        for (String pi : ni) {
            Widget w = new WidgetHSlider(this, 0, y, pi, 8, layer.shaper.paramsI.get(pi), layer.shaper.paramsImin.get(pi), layer.shaper.paramsImax.get(pi), true);
            shaperWidgets.put(pi, w);
            addWidget(w);
            y += 1;
        }
        ArrayList<String> nf = new ArrayList<>();
        for (String pf : layer.shaper.paramsF.keySet())
            nf.add(pf);
        Collections.sort(nf);
        for (String pf : nf) {
            Widget w = new WidgetHSlider(this, 0, y, pf, 8, (int)(layer.shaper.paramsF.get(pf)*100), (int)(layer.shaper.paramsFmin.get(pf)*100), (int)(layer.shaper.paramsFmax.get(pf)*100), true);
            shaperWidgets.put(pf, w);
            addWidget(w);
            y += 1;
        }
        ArrayList<String> nb = new ArrayList<>();
        for (String pb : layer.shaper.paramsB.keySet())
            nb.add(pb);
        Collections.sort(nb);
        for (String pb : nb) {
            Widget w = new WidgetRadio(this, 0, y, pb, null, null, (boolean)(layer.shaper.paramsB.get(pb)));
            shaperWidgets.put(pb, w);
            addWidget(w);
            y += 1;
        }
        ArrayList<String> nt = new ArrayList<>();
        for (String pt : layer.shaper.paramsT.keySet())
            nt.add(pt);
        Collections.sort(nt);
        for (String pt : nt) {
            Widget w = new WidgetTerrainpick(this, 0, y, pt, layer.shaper.paramsT.get(pt));
            shaperWidgets.put(pt, w);
            addWidget(w);
            y += 1;
        }
    }

    void makeNewLayer() {
        Layer layer = new Layer();
        layers.add(layer);
        HashMap<String,Shaper> shapers = new HashMap<>();
        shapers.put("Caves", new Caves(area.xsize,area.ysize));
        shapers.put("Mines", new Mines(area.xsize,area.ysize));
        shapers.put("Growdungeon", new Growdungeon(area.xsize,area.ysize));
        shapers.put("Chambers", new Chambers(area.xsize,area.ysize));
        shapers.put("Convochain", new Convochain(area.xsize,area.ysize));
        shapers.put("Ruins", new Ruins(area.xsize,area.ysize));
        shapers.put("Blobs", new Blobs(area.xsize,area.ysize));
        shapers.put("Roads", new Roads(area.xsize,area.ysize));
        shapers.put("Outline", new Outline(area.xsize,area.ysize));
        shapers.put("Connector", new Connector(area.xsize,area.ysize));
        layerShapers.add(shapers);
        layer.shaper = shapers.get("Caves");
        layer.terrain = "null";
        layer.density = 1f;
        updateLayerPicker();
        selectLayer(layers.indexOf(layer));
    }

    void deleteLayer() {
        if (layers.size() < 2) return;
        layers.remove(layerIndex);
        if (layerIndex >= layers.size()) layerIndex = layers.size() - 1;
        updateLayerPicker();
        selectLayer(layerIndex);
        autoRegenerate();
    }

    void moveLayer(int by) {
        int destIndex = layerIndex + by;
        Layer temp = layers.get(destIndex);
        layers.set(destIndex, layers.get(layerIndex));
        layers.set(layerIndex, temp);
        layerIndex = destIndex;
        updateLayerPicker();
        selectLayer(layerIndex);
        autoRegenerate();
    }

    void updateLayerPicker() {
        String[] choices = new String[layers.size()+1];
        int i = 0;
        for (Layer layer : layers) {
            choices[i] = "Layer " + i + ": " + layer.terrain;
            i++;
        }
        choices[layers.size()] = "<new layer>";
        layerPicker.setChoices(choices);
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == regenButton)
            regenerate();
        else if (widget == quitButton)
            quit();
        else if (widget == autoRegenRadio)
            autoRegenRadio.on = !autoRegenRadio.on;
        else if (widget == pruneRadio) {
            pruneRadio.on = !pruneRadio.on;
            layer.pruneDeadEnds = pruneRadio.on;
            autoRegenerate();
        } else if (widget == wipeRadio) {
            wipeRadio.on = !wipeRadio.on;
            layer.wipeSmallRegions = wipeRadio.on;
            autoRegenerate();
        } else if (widget == roundRadio) {
            roundRadio.on = !roundRadio.on;
            layer.roundCorners = roundRadio.on;
            autoRegenerate();
        } else if (widget == invertRadio) {
            invertRadio.on = !invertRadio.on;
            layer.invert = invertRadio.on;
            autoRegenerate();
        }else if (widget == lightNewAmbient)
            makeNewLight(ULight.AMBIENT);
        else if (widget == lightNewPoint)
            makeNewLight(ULight.POINT);
        else if (widget == layerDeleteButton) {
            deleteLayer();
        } else if (widget == layerUpButton) {
            moveLayer(1);
        } else if (widget == layerDownButton) {
            moveLayer(-1);
        } else if (shaperWidgets.containsValue(widget) && widget instanceof WidgetRadio) {
            ((WidgetRadio)widget).on = !((WidgetRadio)widget).on;
            updateShaperFromWidgets();
            autoRegenerate();
        }
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == shaperPicker) {
            selectShaper(shaperPicker.selected());
            autoRegenerate();
        } else if (widget == tabSlider) {
            changeTab(tabSlider.tabs.get(tabSlider.selection));
        } else if (widget == layerPicker) {
            if (layerPicker.selected().equals("<new layer>"))
                makeNewLayer();
            else
                selectLayer(layerPicker.selection);
        } else if (widget == terrainPicker) {
            layer.terrain = terrainPicker.selection;
            autoRegenerate();
        } else if (widget == fillPicker || widget == entrancePicker || widget == exitPicker || widget == doorPicker) {
            autoRegenerate();
        } else if (widget == drawPicker) {
            layer.printMode = drawPicker.selection;
            autoRegenerate();
        } else if (widget == densitySlider) {
            layer.density = (float)densitySlider.value / 100f;
            autoRegenerate();
        } else if (shaperWidgets.containsValue(widget)) {
            updateShaperFromWidgets();
            autoRegenerate();
        }
    }

    @Override
    public void mouseClick() {
        if (isMouseInside())
            super.mouseClick();
        else {
            dragging = true;
            dragCenterX = commander.camera().getCenterColumn();
            dragCenterY = commander.camera().getCenterRow();
            dragStartX = commander.mouseX() - absoluteX();
            dragStartY = commander.mouseY() - absoluteY();
        }
    }

    @Override
    public void animationTick() {
        super.animationTick();
        if (dragging) {
            if (!commander.mouseButton()) {
                dragging = false;
            } else {
                int mouseX = dragStartX - (commander.mouseX() - absoluteX());
                int mouseY = dragStartY - (commander.mouseY() - absoluteY());
                commander.camera().moveTo(dragCenterX + (mouseX / gw()),
                                            dragCenterY + (mouseY / gh()));
            }
        }
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());
    }

    void selectShaper(String selection) {
        removeShaperWidgets();
        layer.shaper = layerShapers.get(layerIndex).get(selection);
        makeShaperWidgets();
        autoRegenerate();
    }

    void selectLayer(int selection) {
        layerPicker.selection = selection;
        removeShaperWidgets();
        layerIndex = selection;
        layer = layers.get(layerIndex);
        int shaperIndex = 0;
        shaperPicker.selectChoice(layer.shaper.name);
        makeShaperWidgets();
        pruneRadio.on = layer.pruneDeadEnds;
        wipeRadio.on = layer.wipeSmallRegions;
        roundRadio.on = layer.roundCorners;
        invertRadio.on = layer.invert;
        terrainPicker.selection = layer.terrain;
        drawPicker.selection = layer.printMode;
        densitySlider.value = (int)(layer.density * 100f);

        removeWidget(layerUpButton);
        removeWidget(layerDownButton);
        if (layerIndex > 0)
            addWidget(layerDownButton);
        if (layerIndex < layers.size() - 1)
            addWidget(layerUpButton);
    }

    void updateShaperFromWidgets() {
        for (String pi : layer.shaper.paramsI.keySet()) {
            int val = ((WidgetHSlider)(shaperWidgets.get(pi))).value;
            layer.shaper.paramsI.put(pi, val);
        }
        for (String pf : layer.shaper.paramsF.keySet()) {
            int val = ((WidgetHSlider)(shaperWidgets.get(pf))).value;
            layer.shaper.paramsF.put(pf, ((float)val)*0.01f);
        }
        for (String pb : layer.shaper.paramsB.keySet()) {
            boolean val = ((WidgetRadio)(shaperWidgets.get(pb))).on;
            layer.shaper.paramsB.put(pb, val);
        }
        for (String pt : layer.shaper.paramsT.keySet()) {
            String val = ((WidgetTerrainpick)(shaperWidgets.get(pt))).selection;
            layer.shaper.paramsT.put(pt, val);
        }
    }

    void autoRegenerate() {
        if (autoRegenRadio.on)
            regenerate();
    }

    void regenerate() {
        //if (true) return;
        UModalLoading lmodal = new UModalLoading();
        lmodal.setChildPosition(2,2,commander.camera());
        commander.showModal(lmodal);
        commander.renderer.render();

        if (area.xsize != areaWidthSlider.value || area.ysize != areaHeightSlider.value) {
            area.initialize(areaWidthSlider.value, areaHeightSlider.value, fillPicker.selection);
            layer.shaper.resize(areaWidthSlider.value-2, areaHeightSlider.value-2);
        }
        scaper.setup(layers, fillPicker.selection, doorPicker.selection, (float)(doorSlider.value)/100f, (float)(lightChanceSlider.value)/100f, roomLights, vaultSetPicker.selected(), entrancePicker.selection, exitPicker.selection, exitDistanceSlider.value);
        scaper.buildArea(area, 1, new String[]{});

        commander.camera().renderLights();
        commander.detachModal(lmodal);
    }

    void quit() {
        saveScaper(scaper, "testscaper");
        escape();
        commander.game().setupTitleScreen();
    }

    void makeNewLight(int type) {
        ULight light = new ULight(UColor.WHITE, 100, 100);
        if (type == ULight.AMBIENT) {
            light.makeAmbient(1, 1);
        }
        light.setPermanent(true);
        roomLights.add(light);
        updateLightList();
    }

    void updateLightList() {
        String[] lightNames = new String[roomLights.size()];
        int i=0;
        for (ULight l : roomLights) {
            String n = "";
            if (l.type == ULight.AMBIENT)
                n = "ambient light";
            else
                n = "point light";
            lightNames[i] = n;
            i++;
        }
        lightList.setOptions(lightNames);
    }
}
