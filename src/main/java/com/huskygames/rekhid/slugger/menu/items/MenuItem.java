package com.huskygames.rekhid.slugger.menu.items;

import java.util.stream.Stream;

public abstract class MenuItem {

    protected double percentageHeight = FIT_TO_PARENT;
    protected double percentageWidth = FIT_TO_PARENT;
    protected int height = USE_DYNAMIC_SIZE;
    protected int width = USE_DYNAMIC_SIZE;
    public static final double FIT_TO_PARENT = -1;
    public static final int USE_DYNAMIC_SIZE = -2;

    private MenuItem parent;

    public MenuItem() {

    }

    public void setParent(MenuItem item) {
        this.parent = item;
    }

    public double getPercentageHeight() {
        return percentageHeight;
    }

    public void setPercentageHeight(double percentageHeight) {
        dirty();
        this.percentageHeight = percentageHeight;
    }

    public double getPercentageWidth() {
        return percentageWidth;
    }

    public void setPercentageWidth(double percentageWidth) {
        dirty();
        this.percentageWidth = percentageWidth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        dirty();
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        dirty();
        this.width = width;
    }

    public void dirty() {
        if (parent != null) {
            parent.dirty();
        }
    }

    public MenuItem getParent() {
        return parent;
    }

    public Stream<? extends MenuItem> stream() {
        return Stream.of(this);
    }
}
