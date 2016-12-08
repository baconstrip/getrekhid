package com.huskygames.rekhid.menu;

import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.menu.Menu;
import com.huskygames.rekhid.slugger.menu.items.*;
import com.huskygames.rekhid.slugger.resource.LoadedImage;
import com.huskygames.rekhid.slugger.resource.Resource;

public class MainMenu extends Menu {

    private final Rekhid parent;

    public MainMenu(Rekhid parent) {
        this.parent = parent;

        LoadedImage background = (LoadedImage) parent
                .getResourceManager().requestResource(Resource.MAIN_MENU_BG);
        setBackground(background.getImage());

        ColumnLayout root = new ColumnLayout();
        setRoot(root);

        root.addElement(new Padding());
        ListLayout list = new ListLayout();
        list.addElement(new Padding());
        list.addElement(new Title("Get Rekhi'd"));

        TextButton button1 = new TextButton("Start Game");
        this.setDefaultSelection(button1);
        list.addElement(button1);

        list.addElement(new TextButton("Options"));
        list.addElement(new Padding());
        root.addElement(list);
        root.addElement(new Padding());

    }

    @Override
    public void tick() {

    }
}
