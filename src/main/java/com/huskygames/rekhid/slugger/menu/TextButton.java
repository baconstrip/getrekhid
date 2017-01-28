package com.huskygames.rekhid.slugger.menu;

import com.huskygames.rekhid.Definitions;

import java.awt.*;

public class TextButton implements MenuItem {

    private final String text;
    private final int x, y;
    private boolean selected;
    private Color color;


    public TextButton(String text, int x, int y, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.selected = false;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D context) {
        if (!context.getFont().equals(Definitions.DISPLAY_FONT)) {
            context.setFont(Definitions.DISPLAY_FONT);
        }

        if (selected) {
            context.setColor(Color.RED);
        }
        else {
            context.setColor(color);
        }

        textWithShadow(context, text, x, y);
    }

    public void setSelected(boolean set){
        this.selected = set;
    }

    private void textWithShadow(Graphics g, String output, int x, int y) {
        g.setColor(Color.WHITE);
        g.drawString(output, x + 2, y + 2);
        g.setColor(Color.BLACK);
        g.drawString(output, x, y);
    }
}
