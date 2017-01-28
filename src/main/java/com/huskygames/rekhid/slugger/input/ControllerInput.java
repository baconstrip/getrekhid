package com.huskygames.rekhid.slugger.input;

import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.actor.Fighter;
import com.huskygames.rekhid.slugger.util.DoublePair;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ControllerInput {

    private static final Logger logger = LogManager.getLogger(ControllerInput.class.getName());
    private final Rekhid parent;
    private HashMap<Controller, Fighter> controls = new HashMap<>();
    private HashMap<Fighter, Queue<ButtonEvent>> queues = new HashMap<>();
    private HashMap<Fighter, DoublePair> sticks = new HashMap<>();
    private KeyboardTranslator keyboardmap = new KeyboardTranslator();
    private GamepadTranslator gamepadmap = new GamepadTranslator();
    private boolean allowJump = true;

    public ControllerInput(Rekhid parent) {
        this.parent = parent;
    }

    public void tick() {
        for (Map.Entry<Controller, Fighter> controller : controls.entrySet()) {
            controller.getKey().poll();
            EventQueue eventQueue = controller.getKey().getEventQueue();
            Event temp = new Event();

            if (controller.getKey().getType() == Controller.Type.KEYBOARD) {
                while (eventQueue.getNextEvent(temp)) {
                    ButtonType type = keyboardmap.translate(temp.getComponent().getIdentifier());
                    if (temp.getValue() == 1) {
                        if (type != null) {
                            ButtonEvent event = new ButtonEvent(type, controller.getValue(), temp.getNanos());
                            queues.get(controller.getValue()).add(event);
                        }
                    }
                }

                if (controller.getKey().getComponent(keyboardmap.getLeft()).getPollData() != 0) {
                    sticks.get(controller.getValue()).setX(-1);
                } else {
                    sticks.get(controller.getValue()).setX(0);
                    if (controller.getKey().getComponent(keyboardmap.getRight()).getPollData() != 0) {
                        sticks.get(controller.getValue()).setX(1);
                    } else {
                        sticks.get(controller.getValue()).setX(0);
                    }
                }


            } else if (controller.getKey().getType() == Controller.Type.GAMEPAD) {
                while (eventQueue.getNextEvent(temp)) {
                    ButtonType type = gamepadmap.translate(temp.getComponent().getIdentifier());
                    if (temp.getValue() == 1) {
                        if (type != null) {
                            ButtonEvent event = new ButtonEvent(type, controller.getValue(), temp.getNanos());
                            queues.get(controller.getValue()).add(event);
                        }
                    }
                }


                if (controller.getKey().getComponent(gamepadmap.getLeftStickY()).getPollData() == -1) {
                    if (allowJump) {
                        ButtonEvent event = new ButtonEvent(ButtonType.JUMP_BUTTON, controller.getValue(), temp.getNanos());
                        queues.get(controller.getValue()).add(event);
                        allowJump = false;
                    }
                }
                if (!allowJump && controller.getKey().getComponent(gamepadmap.getLeftStickY()).getPollData() > -1) {
                    allowJump = true;
                }

                sticks.get(controller.getValue()).setX(controller.getKey().getComponent(gamepadmap.getLeftStickX()).getPollData());
            }
        }
    }

    public void assignController(Controller cont, Fighter ply) {
        this.controls.put(cont, ply);
        this.queues.put(ply, new LinkedBlockingQueue<ButtonEvent>());
        this.sticks.put(ply, new DoublePair(0, 0));
    }

    /**
     * Returns a list of controllers that are currently plugged in and valid for playing the game
     */
    public List<Controller> getValidControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        return Arrays.stream(controllers).filter((controller) -> {
                    boolean isGood = false;
                    Controller.Type type = controller.getType();

                    if (type == Controller.Type.GAMEPAD) {
                        isGood = true;
                    }
                    if (type == Controller.Type.FINGERSTICK) {
                        isGood = true;
                    }
                    if (type == Controller.Type.KEYBOARD) {
                        isGood = true;
                    }
                    return isGood;
                }
        ).collect(Collectors.toList());
    }

    /**
     * Returns a list of events that happened since last consumption,in the order they occurred.
     * Should use "consumeEvents" for consequential actions.
     * <p>
     * DO NOT MODIFY THE RETURNED LIST
     */
    public Queue<ButtonEvent> requestEventsForPlayer(Fighter p) {
        // TODO: actually return this
        return queues.get(p);
    }

    /**
     * "Consumes" the events so that they're taken off the queue, never to be seen again.
     *
     * @param p
     * @return
     */
    public Queue<ButtonEvent> consumeEventsForPlayer(Fighter p) {
        Queue<ButtonEvent> temp = queues.get(p);
        queues.put(p, new LinkedBlockingQueue<ButtonEvent>());

        return temp;
    }

    public DoublePair getStickForPlayer(Fighter p) {
        return sticks.get(p);
    }
}
