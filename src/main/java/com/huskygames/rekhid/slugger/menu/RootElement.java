package com.huskygames.rekhid.slugger.menu;

import com.huskygames.rekhid.slugger.menu.items.MenuItem;

import java.util.stream.Stream;

public class RootElement extends MenuItem {

    private MenuItem child;

    private final Menu owner;

    public RootElement(Menu owner) {
        this.owner = owner;
    }

    public MenuItem getChild() {
        return child;
    }

    @Override
    public Stream<? extends MenuItem> stream() {
        return child.stream();
    }

    public void setChild(MenuItem child) {
        child.setParent(this);
        this.child = child;
    }

    @Override
    public int getHeight() {
        return USE_DYNAMIC_SIZE;
    }

    @Override
    public int getWidth() {
        return USE_DYNAMIC_SIZE;
    }

    @Override
    public double getPercentageHeight() {
        return FIT_TO_PARENT;
    }

    @Override
    public double getPercentageWidth() {
        return FIT_TO_PARENT;
    }

    @Override
    public void dirty() {
        owner.regenerateLayout();
    }
}
