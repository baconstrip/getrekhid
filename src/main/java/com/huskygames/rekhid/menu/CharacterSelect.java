package com.huskygames.rekhid.menu;

import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.menu.Menu;
import com.huskygames.rekhid.slugger.menu.items.*;
import com.huskygames.rekhid.slugger.resource.LoadedImage;
import com.huskygames.rekhid.slugger.resource.Resource;

import java.awt.*;

public class CharacterSelect extends Menu {
    private final Rekhid parent;

    public CharacterSelect(Rekhid parent) {
        this.parent = parent;

        LoadedImage background = (LoadedImage) parent
                .getResourceManager().requestResource(Resource.CHARACTER_SELECT_BG);
        setBackground(background.getImage());
        ListLayout root = new ListLayout();
        this.setRoot(root);

        ColumnLayout top = new ColumnLayout();
        top.addElement(new Padding());
        top.addElement(new Title("Professor Select"));
        top.addElement(new Padding());
        root.addElement(top);

        ColumnLayout professors = new ColumnLayout();
        professors.addElement(new Padding());
        ImageButton leo = new ImageButton(Resource.LEOS_HEAD);
        professors.addElement(leo);
        this.setDefaultSelection(leo);
        professors.addElement(new ImageButton(Resource.KUHLS_HEAD));
        professors.addElement(new Padding());
        root.addElement(professors);

        ColumnLayout buttons = new ColumnLayout();
        buttons.addElement(new Padding());
        buttons.addElement(new TextButton("Select Level"));
        buttons.addElement(new TextButton("Back"));
        buttons.addElement(new Padding());
        root.addElement(buttons);

    }

    @Override
    public void tick() {

    }
}
