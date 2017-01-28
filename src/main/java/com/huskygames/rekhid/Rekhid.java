package com.huskygames.rekhid;

import com.huskygames.rekhid.actor.StickMan;
import com.huskygames.rekhid.slugger.GamePanel;
import com.huskygames.rekhid.slugger.actor.AI.FsmProf;
import com.huskygames.rekhid.slugger.actor.Fighter;
import com.huskygames.rekhid.slugger.input.ControllerInput;
import com.huskygames.rekhid.slugger.physics.PhysicsManager;
import com.huskygames.rekhid.slugger.resource.Resource;
import com.huskygames.rekhid.slugger.resource.ResourceManager;
import com.huskygames.rekhid.slugger.sound.SoundThread;
import com.huskygames.rekhid.slugger.util.DoublePair;
import com.huskygames.rekhid.slugger.util.FileUtilities;
import com.huskygames.rekhid.slugger.world.LevelComputers;
import com.huskygames.rekhid.slugger.world.World;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.text.resources.cldr.sr.FormatData_sr_Latn_ME;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import static com.huskygames.rekhid.actor.Professor.KUHL;
import static com.huskygames.rekhid.actor.Professor.LEO;

public class Rekhid extends JFrame {

    private final static Logger logger = LogManager.getLogger(Rekhid.class);
    private static final String OPERATING_SYSTEM = System.getProperty("os.name");
    private static Rekhid instance;

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
    private final ControllerInput controllerManager;
    private long lastTickTime = 0;
    private GameState state;
    private long tickCount;
    private MainMenu mainMenu;
    private World world;
    private StickMan player1;

    // BRIAN ADDED THINGS
    private StickMan AiPlayer;
    private FsmProf firstAI;
    private LinkedList<StickMan> players;

    private Rekhid() {

        super();
        logger.info("Building main class");

        instance = this;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenHeight = screenSize.height;
        screenWidth = screenSize.width;

        logger.info("Detected screen resolution: " + screenWidth + "x" + screenHeight);

        this.setTitle(Definitions.NAME);

        int computedX = screenWidth / 2 - Definitions.DEFAULT_WIDTH / 2;
        int computedY = screenHeight / 2 - Definitions.DEFAULT_HEIGHT / 2;
        this.setBounds(computedX, computedY, Definitions.DEFAULT_WIDTH, Definitions.DEFAULT_HEIGHT);

        this.setSize(Definitions.DEFAULT_WIDTH, Definitions.DEFAULT_HEIGHT);


        panel = new GamePanel(this);

        this.add(panel);
        this.pack();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        resourceManager = new ResourceManager();

        controllerManager = new ControllerInput(this);
        checkControlDevices();

        // start some physics


        //creates an instance of a player to be used for testing purposes
        player1 = new StickMan(new DoublePair(1000, 150), new DoublePair(0, 0), LEO);

        // BRIAN ADDED THINGS
        // Add new stickman for ai player to control
        AiPlayer = new StickMan(new DoublePair(3000, 150), new DoublePair(0, 0), KUHL);


        players = new LinkedList<StickMan>();
        //Add all created players to the players list
        players.add(player1);
        players.add(AiPlayer);

        // Pass enemy player and AI player to a new AI instance
        firstAI = new FsmProf(1, AiPlayer, player1 );
    }

    public static Rekhid getInstance() {
        return instance;
    }

    public static void main(String[] args) {

        for (Mixer.Info in: AudioSystem.getMixerInfo()) {
            logger.info("Builtin mixers: " + in.toString());
            logger.info("   " + in.getVendor());
        }

        Thread.currentThread().setName("MainThread");

        logger.info("PID: " + ManagementFactory.getRuntimeMXBean().getName());

        Rekhid game = new Rekhid();
        logger.info("Created game instance.");


        for (AudioFileFormat.Type type : AudioSystem.getAudioFileTypes()) {
            logger.info("supported audio: " + type);
        }

        AudioInputStream i2 = null;
        try {
            i2 = AudioSystem.getAudioInputStream(
                    Rekhid.class.getClassLoader().getResourceAsStream("music/meganddia.ogg"));
        } catch (UnsupportedAudioFileException | IOException e) {
            logger.error("Unable to load music", e);
        }
        AudioFormat baseFormat = i2.getFormat();

        AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
                16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

        AudioInputStream dataIn = AudioSystem.getAudioInputStream(targetFormat, i2);


        // get a line from a mixer in the system with the wanted format
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);
        for (Mixer.Info in : AudioSystem.getMixerInfo()) {
            Mixer mix = AudioSystem.getMixer(in);
            if (mix.isLineSupported(info)) {
                SoundThread.getInstance().setOutputMixer(AudioSystem.getMixer(in));
                logger.warn("Choosing audio line: " + in);
            }
            //SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        }

        // Run the sounds
        logger.info("Attempting to start sound thread.");
        SoundThread.getInstance().start();


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

    public ControllerInput getControllerManager() {
        return controllerManager;
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

    public StickMan getPlayer1() {
        return player1;
    }

    private void checkControlDevices() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        logger.info("Detected usable controllers:");
        for (Controller controller : controllerManager.getValidControllers()) {
            logger.info(controller.getName() + "\t" + controller.getType() + "\t" + controller.getPortType());
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

                // STUFF TO RUN ALWAYS

                controllerManager.tick();

                // GAMESTATE SPECIFIC
                switch (state) {
                    case MENU:
                        SoundThread.getInstance().setBackgroundMusic(Resource.MENU_BG_MUSIC);
                        menuTick();
                        //state = GameState.CHARACTER_SELECT;
                        //comment this out to force main menu
                        break;
                    case CHARACTER_SELECT:
                        //characterSelectTick();
                        state = GameState.MATCH;
                        ArrayList<Fighter> fighters = new ArrayList<Fighter>();
                        fighters.add(AiPlayer);
                        fighters.add(player1);
                        world = new World(new LevelComputers(), fighters);

                        PhysicsManager.getInstance().setWorld(world);

                        controllerManager.assignController(controllerManager.getValidControllers().get(0), AiPlayer);
                        //controllerManager.assignController(controllerManager.getValidControllers().get(0), player1);
                        break;
                    case MATCH:
                        //this.setSize(Definitions.DEFAULT_WIDTH, Definitions.DEFAULT_HEIGHT);
                        matchTick();

                        if (world.getNumLiveFighters() <= 1) {
                            state = GameState.POST_MATCH;
                        }
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
        world.tick();
        PhysicsManager.getInstance().updateObjects();
        // Update AI
        firstAI.update();
    }

    private void characterSelectTick() {
    }

    private void menuTick() {
        if (mainMenu == null) {
            mainMenu = new MainMenu(this);
        }
        mainMenu.onTick();
    }

    public GameState getGameState() {
        return state;
    }

    public void setGameState(GameState newState){
        this.state = newState;
    }

    public World getWorld() {
        return world;
    }

    public long getTickCount() {
        return tickCount;
    }

    public void setControllerForPlayer1(int controllerNumber){
        controllerManager.assignController(controllerManager.getValidControllers().get(controllerNumber), player1);
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
