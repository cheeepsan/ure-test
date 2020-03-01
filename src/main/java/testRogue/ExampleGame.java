package testRogue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import testRogue.actors.TestUPlayer;
import testRogue.json.PlayerCharacter;
import testRogue.map.TestCartographer;
import testRogue.ui.TitleScreen;
import testRogue.ui.character.CreateCharacterForm;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.areas.UCell;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.*;
import ure.terrain.UTerrainCzar;
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
    static TestUPlayer player;
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

        cartographer = new TestCartographer();
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
        area = cartographer.getTitleArea();
        camera.moveTo(area, 50, 50);
        commander.config.setVisibilityEnable(false);
        commander.showModal(new CreateCharacterForm(22, 22, this, "character", area, this));
    }

    public void hearModalTitleScreen(String context, String optional) {
        if (context.equals("Credits") || context.equals("Quit")) {
            commander.quitGame();
        } else {
            if(context.equals("character")) {
                commander.detachModal();
                setupCharacterForm();
            } else if(context.equals("main menu")) {
                commander.detachModal();
                setupTitleScreen();
            } else if(context.equals("start")){
                commander.detachModal();
            }
        }
    }

    public void startGame(String playername, PlayerCharacter character) {
        cartographer.wipeWorld();
        area.requestCloseOut();
        player = (TestUPlayer)commander.loadPlayer();
        if (player == null) {
            player = (TestUPlayer)makeNewPlayer(playername, character);
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

    public UPlayer makeNewPlayer(String playername, PlayerCharacter character) {
        log.debug("Creating a brand new @Player");
        player = new TestUPlayer("Player",new UColor(0.1f, 0.1f, 0.4f), 2, 3, character);
        player.setName(playername);
        player.setID(commander.generateNewID(player));

        updateStatusPanel();

        return player;
    }

    public void updateStatusPanel() {
        statusPanel.addText("Name", player.name(),0,0);
        statusPanel.addText("Money", player.getMoney(),0,1);
        statusPanel.addText("Energy", player.getEnergy(),0,2);
        statusPanel.addText("Time", "", 0, 3);
        statusPanel.addText("Location", "?", 0, 4);
        statusPanel.addText("Turn", "?", 0, 5);
    }
}
