package com.huskygames.rekhid.slugger.menu;

import com.huskygames.rekhid.slugger.Drawable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.HashSet;

public abstract class Menu implements Drawable {
    private final Logger logger = LogManager.getLogger(Menu.class.getName());
    private final HashSet<MenuItem> items = new HashSet<>();
    protected Image background;






    public void draw(Graphics2D context) {

        if (background != null) {
            context.drawImage(background, 0, 0, null);
        }

        for (MenuItem item : items) {
            item.draw(context);
        }
    }

    public HashSet<MenuItem> getItems() {
        return items;
    }

    public void setBackground(Image background) {
        this.background = background;
    }
}
