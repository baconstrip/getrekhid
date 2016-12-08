package com.huskygames.rekhid.slugger.menu;

import com.huskygames.rekhid.slugger.menu.items.MenuItem;

public class ItemDecorator {
    private final MenuItem describes;

    private int computedWidth;
    private int computedHeight;
    private float percentWidth;

    private float xOffset;
    private float yOffset;
    private float percentHeight;

    public ItemDecorator(MenuItem describes) {
        this.describes = describes;
    }

    public float getxOffset() {
        return xOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setyOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public float getPercentHeight() {
        return percentHeight;
    }

    public void setPercentHeight(float percentHeight) {
        this.percentHeight = percentHeight;
    }

    public float getPercentWidth() {
        return percentWidth;
    }

    public void setPercentWidth(float percentWidth) {
        this.percentWidth = percentWidth;
    }

    public int getComputedWidth() {
        return computedWidth;
    }

    public void setComputedWidth(int computedWidth) {
        this.computedWidth = computedWidth;
    }

    public int getComputedHeight() {
        return computedHeight;
    }

    public void setComputedHeight(int computedHeight) {
        this.computedHeight = computedHeight;
    }
}
