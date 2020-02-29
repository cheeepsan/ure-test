package testRogue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import testRogue.commander.TestUCommander;
import testRogue.map.TestCatrographer;
import testRogue.ui.TitleScreen;
import testRogue.ui.character.CreateCharacterForm;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.areas.UCell;
import ure.examplegame.ExampleCartographer;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.*;
import ure.terrain.UTerrainCzar;
import ure.things.UThing;
import ure.things.UThingCzar;
import ure.ui.UCamera;
import ure.ui.modals.HearModalTitleScreen;
import ure.ui.panels.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class ExampleGame implements UREgame, HearModalTitleScreen {

    static UArea area;
    static UCamera camera;
    static UPlayer player;
    static StatusPanel statusPanel;
    static HotbarPanel hotbarPanel;
    static ScrollPanel scrollPanel;
    static LensPanel lensPanel;
    static ActorPanel actorPanel;
    static UREWindow window;

    @Inject
    URenderer renderer;
    @Inject
    UCommander commander;
    @Inject
    UConfig config;
    @Inject
    UTerrainCzar terrainCzar;
    @Inject
    UThingCzar thingCzar;
    @Inject
    UActorCzar actorCzar;
    @Inject
    UCartographer cartographer;

    private Log log = LogFactory.getLog(ure.examplegame.ExampleGame.class);

    public ExampleGame() {
        // Set up logging before doing anything else, including dependency injection.  That way we'll
        // get proper logging for @Provides methods.
        try {
            InputStream configInputStream = ExampleGame.class.getResourceAsStream("/logging.properties");
            LogManager.getLogManager().readConfiguration(configInputStream);
        } catch (IOException ioe) {
            throw new RuntimeException("Can't configure logger", ioe);
        }

        Injector.getAppComponent().inject(this);
    }

    private void makeWindow() {

        window = new UREWindow();
        camera = new UCamera(0, 0, 1200, 800);
        camera.moveTo(area, 40,20);
        window.setCamera(camera);

        UColor borderColor = UColor.DARKGRAY;

        statusPanel = new StatusPanel(10, 10, config.getTextColor(), null, borderColor);

        statusPanel.addText("statusPanel1", "col 0",0,0);
        statusPanel.addText("statusPanel2", "col 1",0,1);
        statusPanel.addText("statusPanel3", "col 2",0,2);
        statusPanel.addText("statusPanel4", "col 5", 0, 5);
        statusPanel.setLayout(UPanel.XPOS_LEFT, UPanel.YPOS_BOTTOM, 8, 0.15f, 12, 10, 0f, 10);
        window.addPanel(statusPanel);

        actorPanel = new ActorPanel(10,10,config.getTextColor(), null, borderColor);
        actorPanel.setLayout(UPanel.XPOS_LEFT, UPanel.YPOS_FIT, 8, 0.15f, 12, 1, 1f, 9999);
        window.addPanel(actorPanel);

        lensPanel = new LensPanel(camera, 0, 0, 12, 12, config.getTextColor(), null, borderColor);
        lensPanel.setLayout(UPanel.XPOS_LEFT, UPanel.YPOS_TOP, 8, 0.15f, 12, 6, 0f, 6);
        window.addPanel(lensPanel);


        scrollPanel = new ScrollPanel(12, 12, config.getTextColor(), null, new UColor(0.3f,0.3f,0.3f));
        scrollPanel.setLayout(UPanel.XPOS_FIT, UPanel.YPOS_BOTTOM, 0, 1f, 9999, 2, 0.18f, 11);
        scrollPanel.addLineFade(new UColor(1.0f, 1.0f, 1.0f));
        scrollPanel.addLineFade(new UColor(0.8f, 0.8f, 0.8f));
        scrollPanel.addLineFade(new UColor(0.6f, 0.6f, 0.6f));
        scrollPanel.addLineFade(new UColor(0.5f, 0.5f, 0.5f));
        scrollPanel.addLineFade(new UColor(0.4f, 0.4f, 0.4f));
        scrollPanel.addLineFade(new UColor(0.3f, 0.3f, 0.3f));
        scrollPanel.print("Welcome to UnRogueEngine!");
        scrollPanel.print("The universal java toolkit for roguelike games.");
        scrollPanel.print("Your journey begins...");
        window.addPanel(scrollPanel);

        hotbarPanel = new HotbarPanel(config.getTextColor(), config.getPanelBgColor());
        hotbarPanel.setLayout(UPanel.XPOS_FIT, UPanel.YPOS_TOP, 1, 1f, 9999, 2, 0f, 2);
        window.addPanel(hotbarPanel);

        window.doLayout();
        renderer.setRootView(window);

        commander.setStatusPanel(statusPanel);
        commander.setScrollPanel(scrollPanel);
        commander.registerCamera(camera);
    }

    public void startUp()  {

        cartographer = new TestCatrographer();
        makeWindow();
        commander.registerComponents(this, window, player, renderer, thingCzar, actorCzar, cartographer);


        commander.registerScrollPrinter(scrollPanel);
        commander.addAnimator(camera);

        setupTitleScreen();

        commander.gameLoop();
    }

    public void setupTitleScreen() {
        window.hidePanels();
        area = cartographer.getTitleArea();
        camera.moveTo(area, 50, 50);
        commander.config.setVisibilityEnable(false);
        commander.showModal(new TitleScreen(22, 22, this, "start", area));
    }

    public void setupCharacterForm() {
        window.hidePanels();
        area = cartographer.getTitleArea();
        camera.moveTo(area, 50, 50);
        commander.config.setVisibilityEnable(false);
        commander.showModal(new CreateCharacterForm(22, 22, this, "character", area));

    }

    public void hearModalTitleScreen(String context, String optional) {
        if (context.equals("Credits") || context.equals("Quit")) {
            commander.quitGame();
        } else {
            if (context.equals("New World")) {
                cartographer.wipeWorld();
                continueGame(optional);
            } else if(context.equals("character")) {
                setupCharacterForm();
            } else {
                continueGame(optional);
            }
        }
    }

    public void continueGame(String playername) {
        area.requestCloseOut();
        player = commander.loadPlayer();
        if (player == null) {
            player = makeNewPlayer(playername);
            log.debug("Getting the starting area");
            cartographer.startLoader();
            area = cartographer.makeStartArea();
            UCell startcell = area.randomOpenCell(player);
            player.setSaveLocation(area, startcell.x, startcell.y);
        } else {
            log.info("Loading existing player into " + player.getSaveAreaLabel());
            cartographer.startLoader();
            area = cartographer.getArea(player.getSaveAreaLabel());
        }
        commander.startGame(player, area);
        player.attachCamera(camera, config.getCameraPinStyle());
        window.showPanels();
    }

    public UPlayer makeNewPlayer(String playername) {
        log.debug("Creating a brand new @Player");
        player = new UPlayer("Player",new UColor(0.1f, 0.1f, 0.4f), 2, 3);
        player.setName(playername);
        player.setID(commander.generateNewID(player));


        return player;
    }
}
