package com.huskygames.rekhid.menu;

import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.menu.Menu;
import com.huskygames.rekhid.slugger.menu.items.*;
import com.huskygames.rekhid.slugger.menu.items.Label;
import com.huskygames.rekhid.slugger.resource.LoadedImage;
import com.huskygames.rekhid.slugger.resource.Resource;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ControllerSelect extends Menu {
    private final Rekhid parent;


    private static class Controller extends CustomMenuItem {
        private final int number;

        private ControllerType type = null;

        public Controller(int i) {
            this.number = i;
        }

        @Override
        public void draw(Graphics2D graphics, int x, int y, int height, int width) {
            graphics.setColor(Color.magenta);
            FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());

            BufferedImage controller = ((LoadedImage) Rekhid.getInstance().getResourceManager()
                    .requestResource(Resource.CONTROLLER_CLIPART)).getImage();
            BufferedImage keyboard = ((LoadedImage) Rekhid.getInstance().getResourceManager()
                    .requestResource(Resource.KEYBOARD_CLIPART)).getImage();


            String name = Integer.toString(number);
            int outHeight = metrics.getHeight();
            graphics.drawString("Player " + number, x, y + outHeight);

            y = y + outHeight;

            int imgHeight = height - outHeight - 20;
            int imgWidth = width - 20;
            int imgx = x + 10;
            int imgy = y + 10;

            if (type == ControllerType.KEYBOARD) {
                graphics.drawImage(keyboard, imgx, imgy, imgWidth, imgHeight, null);
            }
            else if (type == ControllerType.CONTROLLER) {
                graphics.drawImage(controller, imgx, imgy, imgWidth, imgHeight, null);
            }
        }
    }


    public ControllerSelect(Rekhid rekhid) {
        this.parent = rekhid;
        this.setBackground(((LoadedImage) Rekhid.getInstance().getResourceManager()
                .requestResource(Resource.CONTROLLER_SELECT_BG)).getImage());

        ListLayout root = new ListLayout();
        this.setRoot(root);

        ColumnLayout title = new ColumnLayout();
        title.addElement(new Padding());
        title.addElement(new Title("Assign Controller/Keyboard"));
        title.addElement(new Padding());
        root.addElement(title);

        ColumnLayout label = new ColumnLayout();
        label.addElement(new Padding());
        label.addElement(new Label("Press a on keyboard or A on controller to assign player"));
        label.addElement(new Padding());
        root.addElement(label);

        ColumnLayout controllers = new ColumnLayout();
        controllers.addElement(new Padding());
        controllers.addElement(new Controller(1));
        controllers.addElement(new Controller(2));
        controllers.addElement(new Controller(3));
        controllers.addElement(new Controller(4));
        controllers.addElement(new Padding());
        root.addElement(controllers);
        root.addElement(new Padding());
    }

    @Override
    public void tick() {

    }

    public enum ControllerType {
        CONTROLLER,
        KEYBOARD
    }

}
