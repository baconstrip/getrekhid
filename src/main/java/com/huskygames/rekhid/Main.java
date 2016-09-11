package com.huskygames.rekhid;

import com.huskygames.rekhid.slugger.input.ControllerInput;
import com.huskygames.rekhid.slugger.GamePanel;
import com.huskygames.rekhid.slugger.resource.ResourceManager;
import com.huskygames.rekhid.slugger.sound.SoundThread;
import com.huskygames.rekhid.slugger.util.FileUtilities;
import net.java.games.input.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashSet;

public class Main extends JFrame {

    private final static Logger logger = LogManager.getLogger(Main.class);
    private static final String OPERATING_SYSTEM = System.getProperty("os.name");

    static {
        HashSet<String> files = new HashSet<>();

        files.add("jinput-dx8.dll");
        files.add("jinput-dx8_64.dll");
        files.add("jinput-raw.dll");
        files.add("jinput-raw_64.dll");
        files.add("jinput-wintab.dll");
        files.add("libjinput-linux.so");
        files.add("libjinput-linux64.so");
        files.add("libjinput-osx.jnilib");
        for (String file : files) {
            try {
                FileUtilities.copyFileOutOfJar(file, file);
            } catch (IOException e) {
                logger.warn("Failed to export controller libraries. Jinput will not load.");
                e.printStackTrace();
            }
        }
    }

    private final Object gameLock = new Object();
    private final GamePanel panel;
    private final int screenWidth;
    private final int screenHeight;
    private final ResourceManager resourceManager;
    private long lastTickTime = 0;
    private GameState state;
    private long tickCount;
    private MainMenu mainMenu;
    private ControllerInput input;

    public Main() {
        super();
        logger.info("Building main class");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenHeight = screenSize.height;
        screenWidth = screenSize.width;

        logger.info("Detected screen resolution: " + screenWidth + "x" + screenHeight);

        this.setTitle(Definitions.NAME);
        //this.getContentPane().setLayout(null);

        int computedX = screenWidth / 2 - Definitions.DEFAULT_WIDTH / 2;
        int computedY = screenHeight / 2 - Definitions.DEFAULT_HEIGHT / 2;
        this.setBounds(computedX, computedY, Definitions.DEFAULT_WIDTH, Definitions.DEFAULT_HEIGHT);


        panel = new GamePanel(this);

        this.add(panel);
        this.pack();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        checkControlDevices();
        resourceManager = new ResourceManager();
    }

    public static void main(String[] args) {
        Main game = new Main();
        logger.info("Created game instance.");

        // Run the sounds
        SoundThread.getInstance().start();
        logger.info("Started sound thread.");

        for (AudioFileFormat.Type type : AudioSystem.getAudioFileTypes()) {
            logger.info("supported audio: " + type);
        }

        while (game.state != GameState.QUITTING) {
            game.state = GameState.STARTING;
            try {
                game.runGameLoop();
            } catch (Exception e) {
                logger.warn("Exception caught from main thread, restarting interface.", e);
            }
        }
        // cleanup here

    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public long getLastTickTime() {
        return lastTickTime;
    }

    public Object getGameLock() {
        return gameLock;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    private void checkControlDevices() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        logger.info("Detected usable controllers:");
        for (Controller controller : controllers) {
            logger.error(controller.getName() + "\t" + controller.getType() + "\t" + controller.getPortType());
        }
    }

    private void runGameLoop() throws Exception {
        state = Definitions.DEFAULT_STATE;

        long lastTick = System.currentTimeMillis();


        while (true) {
            // game loop
            panel.repaint();

            // This is terrible multithreading code, but it sure is easy.
            synchronized (gameLock) {
                // ALL CODE THAT CHANGES THE GAME MUST BE IN HERE.
                switch (state) {
                    case MENU:
                        menuTick();
                        break;
                    case CHARACTER_SELECT:
                        characterSelectTick();
                        break;
                    case MATCH:
                        matchTick();
                        break;
                    case POST_MATCH:
                        break;
                    case QUITTING:
                        return;
                }
            }
            // manage FPS
            long currentTime = System.currentTimeMillis();
            long tickTime = currentTime - lastTick;
            lastTickTime = tickTime;
            if (tickTime < (long) Definitions.TARGET_TIME) {
                try {
                    Thread.sleep(((long) Definitions.TARGET_TIME) - tickTime);
                } catch (InterruptedException e) {
                    logger.warn("Game thread interrupted, quitting.");
                    state = GameState.QUITTING;
                    return;
                }
            } else {
                logger.warn("Detected slow ticking of game world. Took: " + tickTime + "ms, " +
                        "expected " + Definitions.TARGET_TIME + "ms or less for tick #"
                        + tickCount);
            }
            tickCount++;
            lastTick = System.currentTimeMillis();

            // end game loop
        }
    }

    private void matchTick() {
    }

    private void characterSelectTick() {
    }

    private void menuTick() {
        if (mainMenu == null) {
            mainMenu = new MainMenu(this);
        }
    }

    public GameState getGameState() {
        return state;
    }

    public enum GameState {
        STARTING,
        MENU,
        CHARACTER_SELECT,
        MATCH,
        POST_MATCH,
        QUITTING
    }
}
