package com.huskygames.rekhid.slugger.menu.items;

import com.huskygames.rekhid.Definitions;

import java.awt.*;

public class TextButton extends MenuItem implements Selectable{

    private String text;
    private final Font font;

    private boolean selected;

    public TextButton(String text) {
        this.text = text;
        font = Definitions.DISPLAY_FONT;
    }

    public TextButton(String text, Font font) {
        this.text = text;
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
