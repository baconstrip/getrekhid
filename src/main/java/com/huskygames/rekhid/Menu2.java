package com.huskygames.rekhid;

import com.huskygames.rekhid.slugger.menu.Menu;
import com.huskygames.rekhid.slugger.menu.MenuItem;
import com.huskygames.rekhid.slugger.menu.TextButton;
import com.huskygames.rekhid.slugger.resource.LoadedImage;
import com.huskygames.rekhid.slugger.resource.Resource;

import java.awt.*;

/**
 * Created by alex on 1/21/2017.
 */
public class Menu2 extends Menu{
    private final Rekhid parent;

    public Menu2(Rekhid parent) {
        this.parent = parent;

        LoadedImage background = (LoadedImage) parent.getResourceManager().requestResource(Resource.MAIN_MENU_BG);
        setBackground(background.getImage());

        TextButton item1 = new TextButton("one", 50, 50, Color.BLUE);
        item1.setSelected(true);
        TextButton item2 = new TextButton("two", 50, 250, Color.BLUE);
        TextButton item3 = new TextButton("three", 50, 450, Color.BLUE);

        getItems().add(item1);
        getItems().add(item2);
        getItems().add(item3);
    }
}
