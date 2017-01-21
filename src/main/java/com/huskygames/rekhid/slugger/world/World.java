package com.huskygames.rekhid.slugger.world;

import com.huskygames.rekhid.Definitions;
import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.actor.Projectile;
import com.huskygames.rekhid.slugger.Drawable;
import com.huskygames.rekhid.slugger.Positionable;
import com.huskygames.rekhid.slugger.actor.Actor;
import com.huskygames.rekhid.slugger.actor.Fighter;
import com.huskygames.rekhid.slugger.physics.Collidable;
import com.huskygames.rekhid.slugger.physics.PhysicsManager;
import com.huskygames.rekhid.slugger.util.DoublePair;
import com.huskygames.rekhid.slugger.util.IntPair;
import com.huskygames.rekhid.slugger.util.SpriteUtilities;
import com.huskygames.rekhid.slugger.util.collison.shape.Circle;
import com.huskygames.rekhid.slugger.util.collison.shape.Rectangle;
import com.huskygames.rekhid.slugger.util.collison.shape.Shape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * The world object, contains the grid, and all the objects on the grid.
 */
public class World implements Drawable {

    //holds the height, in case it's not default
    //width is known no matter what, as aspect ratio is kept at 16:9
    private int height = Definitions.DEFAULT_WORLD_SIZE.getX();

    //private Grid grid;
    private List<Fighter> fighters = new ArrayList<>(4);

    //level contains its own colliders
    private Level level;

    private ViewPort viewPort;
    private Logger logger = LogManager.getLogger(World.class.getName());

    private int[] sizeAlternator = new int[]{1500, 1550, 1600, 1650, 1700, 1750, 1800, 1850, 1900, 1950};

    /**
     * Constructs the world object with default height and width
     *
     * @param level: The Level that will be running on this world
     */
    public World(Level level, List<Fighter> players) {
        //grid = new Grid(Definitions.DEFAULT_WIDTH, Definitions.DEFAULT_HEIGHT);

        for (int i=0; i<players.size(); i++){
            PhysicsManager.getInstance().addObject(players.get(i));
            fighters.add(players.get(i));
            //Players should start at the location specified in their constructor, not in a level.getStartPos() ??
            //players.get(i).setPosition(level.getStartPos()[i].asDoublePair());
            PhysicsManager.getInstance().addObject(fighters.get(i));
        }

        //defined level
        this.level = level;

        //starting viewport
        viewPort = level.getDefaultViewPort();

        Rekhid.getInstance().getResourceManager().suggestLoad(level.getBackground());
    }

/*    *//**
     * Constructs the world using abnormal height
     *
     * @param height:   the custom height used in this world
     * @param theLevel: the level that will run on this world
     *//*
    public World(int height, Level theLevel, int viewHeight, int viewTLX, int viewTLY, StickMan player) {
        //grid = new Grid(width, height);
        this.height = height;

        fighters = new StickMan[4];
        for (int i = 0; i < 4; i++) {
            fighters[i] = null;
        }
        fighters[0] = player;
        level = theLevel;

        viewPort = new ViewPort(viewHeight, new IntPair(viewTLX, viewTLY), height);
    }*/


    /**
     * Returns the scale of the view, based on the height of the viewport and the height of the world
     *
     * @return a double of the scale of the viewport to the world
     * NOTE: the scale should never be less than 1. This is strictly to blow up the image when the viewport is zoomed in.
     * The largest the viewport can be is the size of the world itself.
     */
    public double scaleView() {
        return height / (double) viewPort.getHeight();
    }

    /**
     * This is the ratio between grid units and pixels
     */
    public double getViewRatio() {
        return viewPort.getHeight() / (double) viewPort.getWindowSize().getY();
    }

    /**
     * Returns the position of the item in the frame, in grid units
     */
    private DoublePair translatePosition(Positionable pos) {
        return translatePositionPair(pos.getPosition());
    }

    private DoublePair translatePositionPair(DoublePair pair) {
        return pair.subtract(viewPort.getTopLeft());
    }

    /**
     * Returns a number from that represents how large it is, relative to the viewport
     * (must be the height).
     * for example: if the view port is 500, and 100 is passed in, it will be 0.2
     */
    private double percentOfHeight(int gridSize) {
        return ((double) gridSize) / viewPort.getHeight();
    }

    private int getPixelHeight(int gridSize) {
        return (int) ((percentOfHeight(gridSize) * gridSize * scaleView()));
    }

    private int centrer(double pos, double in) {
        return (int) Math.round(pos - in / 2);
    }

    /**
     * Draws a small sphere representing the centre of an actor, given the position in pixels
     */
    private void drawCenter(IntPair position, Graphics2D context) {
        Color temp = context.getColor();
        context.setColor(Definitions.CENTRE_COLOR);
        context.fillOval(position.getX() - 5, position.getY() - 5, 10, 10);
        context.setColor(temp);
    }

    private void drawActor(Actor actor, Graphics2D context) {
        DoublePair centre = translatePosition(actor);
        IntPair pixelCenter = centre.multiply(1 / getViewRatio()).rounded();
        double heightInPix = actor.getHeight() * (1 / getViewRatio());
        double widthInPix = heightInPix * actor.getAspectRatio();

        //noinspection Simplify,PointlessBooleanExpression,ConstantConditions
        if (Rekhid.getInstance().getTickCount() % 60 == 0 && Definitions.NOISY_RENDER) {
            logger.info("Viewport height, in px: " + viewPort.getWindowSize().getY() + " viewport height, in gridunits: " + viewPort.getHeight());
            logger.info("Viewport width, in GU: " + viewPort.getHeight() * 1.7777777777);
            logger.info("Therefore, our gridunit to pixel ratio is: " + getViewRatio());
            logger.info("The actor is at relative grid position of: "
                    + actor.getRelativePosition(viewPort) + " given a grid that starts at " + viewPort.getTopLeft());
            int top = centrer(actor.getPosition().getY(), actor.getHeight());
            logger.info(" Given the actor's height, he will go from " + top + " to " + (top + actor.getHeight()));
            logger.info(" Since the actor is " + actor.getHeight() + "/" + viewPort.getHeight() +
                    " he will be " + getPixelHeight(actor.getHeight()) + " pixels high, or " +
                    actor.getHeight() / (double) viewPort.getHeight() * 100 + "%");
        }

        AffineTransformOp scaler = SpriteUtilities.buildScaleOp(
                actor.getSprite().getHeight(null), (int) heightInPix);


        context.drawImage(actor.getSprite(), scaler,
                centrer(pixelCenter.getX(), heightInPix),
                centrer(pixelCenter.getY(), widthInPix));

        if (Definitions.DRAW_CENTRES) {
            drawCenter(pixelCenter, context);
        }

        if (Definitions.DRAW_HITBOXES) {
            drawHitbox(actor, context);
        }
    }

    private void drawHitbox(Collidable actor, Graphics2D context) {
        Color temp = context.getColor();
        Set<Shape> shapes = actor.getCollisions();
        context.setColor(Definitions.HITBOX_COLOR);
        for (Shape shape : shapes) {
            DoublePair position = translatePosition(shape);

            DoublePair pixelCenter = position.multiply(1 / getViewRatio());
            if (shape instanceof Circle) {
                int radiusInPx = (int) (((Circle) shape).getRadius() * (1 / getViewRatio()));
                int diamInPx = radiusInPx * 2;
                context.fillOval(centrer(pixelCenter.getX(), diamInPx),
                        centrer(pixelCenter.getY(), diamInPx), diamInPx, diamInPx);
                //noinspection Simplify,PointlessBooleanExpression,ConstantConditions
                if (Rekhid.getInstance().getTickCount() % 60 == 0 && Definitions.NOISY_RENDER) {
                    logger.info("   my radius in pixels is: " + radiusInPx);
                    logger.info("   drawing circle at: " + pixelCenter);

                }
            } else if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                IntPair max = rectangle
                        .getMax()
                        .subtract(viewPort.getTopLeft())
                        .multiply(1 / getViewRatio())
                        .rounded();
                IntPair min = rectangle
                        .getMin()
                        .subtract(viewPort.getTopLeft())
                        .multiply(1 / getViewRatio())
                        .rounded();
                IntPair size = max.subtract(min);

                context.fillRect(min.getX(), min.getY(), size.getX(), size.getY());
            }
        }
        if (actor instanceof Fighter && !(actor instanceof Projectile)) {
            shapes = ((Fighter) actor).getPain();
            context.setColor(Definitions.HURTBOX_COLOR);
            for (Shape shape : shapes) {
                DoublePair position = translatePosition(shape);

                DoublePair pixelCenter = position.multiply(1 / getViewRatio());
                int radiusInPx = (int) (((Circle) shape).getRadius() * (1 / getViewRatio()));
                int diamInPx = radiusInPx * 2;
                context.fillOval(centrer(pixelCenter.getX(), diamInPx),
                        centrer(pixelCenter.getY(), diamInPx), diamInPx, diamInPx);
                //noinspection Simplify,PointlessBooleanExpression,ConstantConditions
                if (Rekhid.getInstance().getTickCount() % 60 == 0 && Definitions.NOISY_RENDER) {
                    logger.info("   my radius in pixels is: " + radiusInPx);
                    logger.info("   drawing circle at: " + pixelCenter);

                }
            }
        }

        context.setColor(temp);
    }

    public void drawBackground(Graphics2D context) {
        int levelHeight = level.getLevelSize().getY(); // the full size of level, in GU
        int levelImageHeight = level.getBackgroundImage().getHeight(); // the height of the bg image in px
        int viewPortHeight = viewPort.getHeight(); // the height of the view port, in GU

        double vr = levelHeight / (double) viewPortHeight;

        double pxh = vr * levelImageHeight;
        double pxw = vr * level.getBackgroundImage().getWidth();

        IntPair bgPos = viewPort.getTopLeft().neg().mul(1 / getViewRatio()).rounded();
        context.drawImage(level.getBackgroundImage(), bgPos.getX(), bgPos.getY(), (int) pxw, (int) pxh, null);

        if (Definitions.DRAW_HITBOXES) {
            drawHitbox(level, context);
        }
    }

    @Override
    public void draw(Graphics2D context) {

        // draw the background
        drawBackground(context);

        // draw the fighters
        for (Fighter ply : fighters) {
            if (ply != null && !ply.isDead()) {
                drawActor(ply, context);
            }
            if (ply.hasProjectile()){
                drawActor(ply.getProjectile(), context);
            }
        }
    }

    public void tick() {

        //int index = (int) (Rekhid.getInstance().getTickCount() / sizeAlternator.length) % sizeAlternator.length;
        //this.viewPort.setHeight(sizeAlternator[index]);
        for (Fighter ply : fighters) {
            if (ply != null) {
                ply.tick();
            }
        }
        updateViewport();

        for (Fighter ply : fighters) {
            // first, check to see if they've left the world
            DoublePair lowerBound = level.getUpperLeftPlayableArea().asDoublePair();
            DoublePair upperBound = level.getLowerRightPlayableArea().asDoublePair();

            //logger.warn("POS of " + ply.getName() + ": " + ply.getPosition());
            if (!ply.getPosition().isInAabb(lowerBound, upperBound)) {
                // kill em!
                if (ply.getLives() > 0) {
                    ply.removeLife();
                    ply.setJumps(0);
                    ply.setVelocity(new DoublePair(0, 0));
                }
                ply.setDead(true);
            }

            if (ply.isDead()) {
                ply.setPosition(level.getStartPos()[fighters.indexOf(ply)].asDoublePair());
                // revive!
                if (ply.getLives() > 0) {
                    ply.setDead(false);
                }
            }

        }
    }

    private void updateViewport() {
        double viewRatio = 16 / 9;
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Fighter ply: fighters) {
            if (ply != null && !ply.isDead()) {
                if (ply.getPosition().getX() < minX) {
                    minX = (int) ply.getPosition().getX();
                }
                if (ply.getPosition().getX() > maxX) {
                    maxX = (int) ply.getPosition().getX();
                }

                if (ply.getPosition().getY() < minY) {
                    minY = (int) ply.getPosition().getY();
                }
                if (ply.getPosition().getY() > maxY) {
                    maxY = (int) ply.getPosition().getY();
                }
            }
        }
        minX -= Definitions.VIEWPORT_PADDING;
        minY -= Definitions.VIEWPORT_PADDING;
        maxX += Definitions.VIEWPORT_PADDING;
        maxY += Definitions.VIEWPORT_PADDING;

        IntPair minist = level.getMinViewPort();
        IntPair maxist = level.getMaxViewPort();
        minX = minX > minist.getX() ? minX : minist.getX();
        minY = minY > minist.getY() ? minY : minist.getY();

        maxX = maxX < maxist.getX() ? maxX : maxist.getX();
        maxY = maxY < maxist.getY() ? maxY : maxist.getY();

        if (maxY - minY < level.getMinViewHeight()) {
            int center = minY + ((maxY - minY) / 2);
            minY = center - level.getMinViewHeight() / 2;
            maxY = center + level.getMinViewHeight() / 2;
        }

        int xDis = maxX - minX;
        int yDis = maxY - minY;

        int ypropo = (int) (yDis * viewRatio);

        IntPair topLeft = new IntPair(minX, minY);

        if (ypropo > xDis) {
            // y is the bounding factor
            viewPort.setCorner(topLeft);
            viewPort.setHeight(yDis);
        } else {
            // x is the bounding factor
            viewPort.setCorner(topLeft);
            viewPort.setHeight((int) (xDis / viewRatio));
        }
    }

    public Level getLevel() {
        return level;
    }

    public List<Fighter> getFighters() {
        return fighters;
    }

    public int getNumLiveFighters() {
        int alive = 0;
        for (Fighter ply : fighters) {
            if (!ply.isDead()) {
                alive++;
            }
        }
        return alive;
    }

    public Fighter getWinner() {
        Fighter winner = getFighters().get(0);
        for (Fighter ply : fighters) {
            if (ply.getLives() > winner.getLives()){
                winner = ply;
            }
        }
        return winner;
    }
}
