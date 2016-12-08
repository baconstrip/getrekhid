package com.huskygames.rekhid.slugger.menu.items;


import com.huskygames.rekhid.Definitions;

import java.awt.*;

public class Label extends com.huskygames.rekhid.slugger.menu.items.MenuItem {

    private String label;
    private final Font font;

    public Label(String label){
        this.label = label;
        font = Definitions.DISPLAY_FONT;
    }

    public Label(String label, Font font) {
        this.label = label;
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public String getLabel() {
        return label;
    }
}
