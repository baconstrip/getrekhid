package com.huskygames.rekhid.slugger.menu.items;
import com.huskygames.rekhid.slugger.resource.Resource;


public class ImageButton extends MenuItem implements Selectable {

    private final Resource image;
    private boolean selected;

    public ImageButton(Resource image) {
        this.image = image;
    }

    public Resource getImage() {
        return image;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
