package com.huskygames.rekhid.slugger.menu;

import com.huskygames.rekhid.Definitions;
import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.Drawable;
import com.huskygames.rekhid.slugger.menu.items.*;
import com.huskygames.rekhid.slugger.menu.items.Container;
import com.huskygames.rekhid.slugger.menu.items.Label;
import com.huskygames.rekhid.slugger.menu.items.MenuItem;
import com.huskygames.rekhid.slugger.resource.LoadedImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class Menu implements Drawable {
    private final Logger logger = LogManager.getLogger(Menu.class.getName());
    private final HashSet<MenuItem> items = new HashSet<>();
    private final RootElement root = new RootElement(this);
    protected Image background;
    Map<MenuItem, ItemDecorator> computedValues = new HashMap<>();
    private Selectable currentSelection;
    private boolean layoutDirty = true;

    public void draw(Graphics2D context) {
        if (background != null) {
            context.drawImage(background, 0, 0,
                    Rekhid.getInstance().getWidth(),
                    Rekhid.getInstance().getHeight(), null);
        }

        if (layoutDirty) {
            computeSizes();
        }

        root.stream().forEach(x -> drawItem(x, context));

    }

    public abstract void tick();

    public void setDefaultSelection(Selectable sel) {
        this.currentSelection = sel;
        sel.setSelected(true);
    }

    private void computeSizes() {
        int windowHeight = Rekhid.getInstance().getHeight();
        int windowWidth = Rekhid.getInstance().getWidth();
        MenuItem first = root.getChild();

        ItemDecorator rootdec = new ItemDecorator(root);
        rootdec.setComputedHeight(windowHeight);
        rootdec.setComputedWidth(windowWidth);

        computedValues.put(root, rootdec);

        ItemDecorator firstDec = new ItemDecorator(first);

        firstDec.setyOffset(0);
        firstDec.setxOffset(0);

        if (first.getWidth() == MenuItem.USE_DYNAMIC_SIZE) {
            firstDec.setPercentWidth(1);
            firstDec.setComputedWidth(windowWidth);
        }
        if (first.getHeight() == MenuItem.USE_DYNAMIC_SIZE) {
            firstDec.setPercentHeight(1);
            firstDec.setComputedHeight(windowHeight);
        }

        computedValues.put(first, firstDec);

        sizeContainer(first);
    }

    private void drawItem(MenuItem item, Graphics2D g) {
        Color temp = g.getColor();

        int windowHeight = Rekhid.getInstance().getHeight();
        int windowWidth = Rekhid.getInstance().getWidth();

        ItemDecorator dec = computedValues.get(item);
        float pheight = dec.getPercentHeight();
        float pwidth = dec.getPercentWidth();

        int height = (int) (pheight * windowHeight);
        int width = (int) (pwidth * windowWidth);

        int offsetx = (int) (dec.getxOffset() * windowWidth);
        int offsety = (int) (dec.getyOffset() * windowHeight);

        int centerx = offsetx + width / 2;
        int centery = offsety + height / 2;


        if (item instanceof Padding) {
            if (Definitions.DRAW_PADDING) {
                g.setColor(Definitions.PADDING_COLOR);
                g.fillRect(offsetx, offsety, width, height);
            }
        }

        if (item instanceof Label) {
            Label label = (Label) item;
            g.setColor(Color.blue);
            String output = label.getLabel();
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int outWidth = metrics.stringWidth(output);
            int outHeight = metrics.getHeight();
            g.drawString(output, centerx - outWidth / 2, centery - outHeight / 2);
            // g.fillRect(offsetx, offsety, width, height);
        }

        if (item instanceof TextButton) {
            TextButton button = (TextButton) item;
            String output = button.getText();
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int outWidth = metrics.stringWidth(output);
            int outHeight = metrics.getHeight();

            if (button.isSelected()) {
                g.setColor(Color.YELLOW);
                g.fillRoundRect(offsetx, offsety, width, height, 100, 100);
            }
            g.setColor(Color.BLACK);
            g.fillRoundRect(offsetx + 4, offsety + 4, width - 8, height - 8, 100, 100);
            g.setColor(Color.RED);
            g.drawString(output, centerx - outWidth / 2, centery);
        }

        if (item instanceof Title) {
            Title title = (Title) item;
            String output = title.getTitle();
            FontMetrics metrics = g.getFontMetrics(g.getFont());

            int outWidth = metrics.stringWidth(output);
            int outHeight = metrics.getHeight();

            g.setColor(Color.WHITE);
            g.drawString(output, centerx - outWidth / 2 + 1, centery + 1);
            g.drawString(output, centerx - outWidth / 2 - 1, centery - 1);

            g.setColor(Color.black);
            g.drawString(output, centerx - outWidth / 2, centery);


        }

        if (item instanceof ImageButton) {
            ImageButton imageButton = (ImageButton) item;

            BufferedImage img = ((LoadedImage) Rekhid.getInstance().getResourceManager()
                    .requestResource(imageButton.getImage())).getImage();
            if (imageButton.isSelected()) {
                g.setColor(Color.YELLOW);
                g.fillRect(offsetx, offsety, width, height);
            }

            g.drawImage(img, offsetx + 4, offsety + 4, width - 8, height - 8, null);
        }

        if (item instanceof CustomMenuItem) {
            CustomMenuItem cItem = (CustomMenuItem) item;
            cItem.draw(g, offsetx, offsety, height, width);
        }

        g.setColor(temp);
    }

    private void sizeContainer(MenuItem element) {
        if (element instanceof ColumnLayout) {
            ColumnLayout cols = (ColumnLayout) element;
            // total width of all elements that declare explicit width
            int totalWidth = cols.getElements().stream().map(MenuItem::getWidth)
                    .reduce(0, (total, iw) -> total += iw > 0 ? iw : 0);
            int counter = 0;
            float itemWidth = computedValues.get(cols).getPercentWidth()
                    / cols.getElements().size();
            for (MenuItem item : cols.getElements()) {
                ItemDecorator temp = new ItemDecorator(item);
                temp.setPercentHeight(computedValues.get(item.getParent()).getPercentHeight());
                if (item.getWidth() == MenuItem.USE_DYNAMIC_SIZE) {
                    temp.setPercentWidth(computedValues.get(cols).getPercentWidth()
                            / cols.getElements().size());

                }

                temp.setxOffset(computedValues.get(item.getParent()).getxOffset()
                        + counter * itemWidth);
                temp.setyOffset(computedValues.get(cols).getyOffset());
                counter++;
                computedValues.put(item, temp);

                if (item instanceof Container) {
                    sizeContainer(item);
                }
            }
        }
        if (element instanceof ListLayout) {
            ListLayout rows = (ListLayout) element;
            // total width of all elements that declare explicit width
            int totalHeight = rows.getElements().stream().map(MenuItem::getHeight)
                    .reduce(0, (total, ih) -> total += ih > 0 ? ih : 0);
            int counter = 0;
            float itemHeight = computedValues.get(rows).getPercentHeight()
                    / rows.getElements().size();
            for (MenuItem item : rows.getElements()) {
                ItemDecorator temp = new ItemDecorator(item);
                temp.setPercentWidth(computedValues.get(item.getParent()).getPercentWidth());
                if (item.getHeight() == MenuItem.USE_DYNAMIC_SIZE) {
                    temp.setPercentHeight(computedValues.get(rows).getPercentHeight()
                            / rows.getElements().size());

                }
                temp.setyOffset(computedValues.get(rows).getyOffset()
                        + counter * itemHeight);
                temp.setxOffset(computedValues.get(rows).getxOffset());
                counter++;
                computedValues.put(item, temp);
                if (item instanceof Container) {
                    sizeContainer(item);
                }
            }
        }
    }

    public void setBackground(Image background) {
        this.background = background;
    }

    public void regenerateLayout() {
        layoutDirty = true;
        computedValues.clear();
    }

    public void setRoot(MenuItem root) {
        layoutDirty = true;
        this.root.setChild(root);
    }
}
