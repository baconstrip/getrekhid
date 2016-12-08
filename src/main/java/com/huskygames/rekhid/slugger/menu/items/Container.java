package com.huskygames.rekhid.slugger.menu.items;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public abstract class Container extends MenuItem {
    LinkedList<MenuItem> list = new LinkedList<>();

    public void addElement(MenuItem a){
        dirty();
        a.setParent(this);
        list.add(a);
    }

    public void removeElement(MenuItem item) {
        dirty();
        list.remove(item);
    }

    public List<MenuItem> getElements(){
        return list;
    }

    public Stream<MenuItem> stream() {
        return list.stream().flatMap(MenuItem::stream);
    }
}
