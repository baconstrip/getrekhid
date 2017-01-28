package com.huskygames.rekhid;

import com.huskygames.rekhid.slugger.menu.Menu;

import com.huskygames.rekhid.slugger.input.ControllerInput;
import com.huskygames.rekhid.slugger.menu.TextButton;
import com.huskygames.rekhid.slugger.resource.LoadedImage;
import com.huskygames.rekhid.slugger.resource.Resource;
import net.java.games.input.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;


public class MainMenu extends Menu {

    private static final Logger logger = LogManager.getLogger(MainMenu.class.getName());
    private final Rekhid parent;

    public MainMenu(Rekhid parent) {
        this.parent = parent;

        LoadedImage background = (LoadedImage) parent.getResourceManager().requestResource(Resource.MAIN_MENU_BG);
        setBackground(background.getImage());


    }

    public void onTick(){
        TextButton item1 = new TextButton("PRESS START TO CONTINUE", 300, 400, Color.BLACK);
        getItems().add(item1);

        ControllerInput input = Rekhid.getInstance().getControllerManager();

        Component[] componentList;

        for (int i = 0; i < input.getValidControllers().size(); i++) {
            componentList = input.getValidControllers().get(i).getComponents();
            for (int j = 0; j < 10; j++) {
                input.getValidControllers().get(i).poll();
                if (componentList[j].getPollData() == 1) {
                    Rekhid.getInstance().setControllerForPlayer1(i);
                    Rekhid.getInstance().setGameState(Rekhid.GameState.CHARACTER_SELECT);
                }
            }
        }
    }
}
