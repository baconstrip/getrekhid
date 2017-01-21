package com.huskygames.rekhid.actor;

import com.huskygames.rekhid.Definitions;
import com.huskygames.rekhid.Rekhid;
import com.huskygames.rekhid.slugger.actor.*;
import com.huskygames.rekhid.slugger.input.ButtonEvent;
import com.huskygames.rekhid.slugger.input.ControllerInput;
import com.huskygames.rekhid.slugger.physics.PhysicsManager;
import com.huskygames.rekhid.slugger.resource.LoadedImage;
import com.huskygames.rekhid.slugger.resource.Resource;
import com.huskygames.rekhid.slugger.resource.sprite.SpriteSequence;
import com.huskygames.rekhid.slugger.resource.sprite.SpriteSheet;
import com.huskygames.rekhid.slugger.resource.sprite.SpriteState;
import com.huskygames.rekhid.slugger.util.DoublePair;
import com.huskygames.rekhid.slugger.util.collison.shape.Shape;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * A simple StickMan intended to illustrate the game
 */
public class StickMan extends Fighter {
    private final Professor prof;
    private final SpriteSheet sprite;
    private SpriteState sequence;
    private ControllerInput input;
    private Set<Shape> colliders = new HashSet<>();
    private double speed = .5;
    private double slidiness = 10;
    private DoublePair playerPos;
    private double currentMaxVelocity = Definitions.MAX_VELOCITY;
    private int ticker = -50;

    // declare sprite sequences
    SpriteSequence moveRight = new SpriteSequence(
            new int[]{0, 0, 0, 0,  0,  0, 1, 1, 1},
            new int[]{6, 7, 8, 9, 10, 11, 0, 1, 2},
            new int[]{5, 5, 5, 5,  5,  5, 5, 5, 5}, "moveRight");
    SpriteSequence moveLeft = new SpriteSequence(
            new int[]{0, 0, 0, 0,  0,  0, 1, 1, 1},
            new int[]{6, 7, 8, 9, 10, 11, 0, 1, 2},
            new int[]{5, 5, 5, 5,  5,  5, 5, 5, 5}, "moveLeft");
    SpriteSequence jump = new SpriteSequence(
            new int[]{1,  1,  1,  1,  1,  1},
            new int[]{5,  6,  7,  8,  9, 10},
            new int[]{3, 10, 10, 10, 10, 10}, "jumping");
    SpriteSequence neutralAttack = new SpriteSequence(
            new int[]{3, 3, 3, 3},
            new int[]{4, 5, 4, 4},
            new int[]{5, 5, 5, 5}, "neutralAttack");
    SpriteSequence upAttack = new SpriteSequence(
            new int[]{4, 4, 4, 5, 5},
            new int[]{9,10,11, 0, 1},
            new int[]{4, 4, 4,10, 0}, "upAttack");
    SpriteSequence downAttack = new SpriteSequence(
            new int[]{ 2, 2, 2, 2, 3, 2},
            new int[]{10, 8, 9, 7, 0,11},
            new int[]{ 5, 5, 5, 5, 5, 5}, "downAttack");


    // end sprite sequences

    private static final Logger logger = LogManager.getLogger(StickMan.class.getName());

    public StickMan(DoublePair pos, DoublePair vel, Professor prof) {
        super(pos, vel);
        this.prof = prof;
        this.playerPos = pos;

        this.lifetime = Actor.FOREVER;
        executing = false;
        this.sprite = (SpriteSheet) Rekhid.getInstance().getResourceManager()
                .requestResource(Resource.STICK_MAN);
        this.input = Rekhid.getInstance().getControllerManager();

        createDefaultHitbox();
    }

    private void createDefaultHitbox() {
        colliders.add(new ActorCircle(new DoublePair(0, getHeight() / 4), this, getHeight() / 6));
        colliders.add(new ActorCircle(new DoublePair(0, 0), this, getHeight() / 6));
        colliders.add(new ActorCircle(new DoublePair(0, -getHeight() / 4), this, getHeight() / 6));
    }

    protected SpriteState getSequence(){ return sequence; }

    private BufferedImage getHead() {
        LoadedImage temp = null;
        switch (prof) {
            case KUHL:
                temp = (LoadedImage) Rekhid.getInstance().getResourceManager()
                        .requestResource(Resource.KUHLS_HEAD);
                break;
            case LEO:
                temp = (LoadedImage) Rekhid.getInstance().getResourceManager()
                        .requestResource(Resource.LEOS_HEAD);
        }
        return temp.getImage();
    }

    @Override
    public void tick() {
        //ticker++;
        //ticker %= 200;
        //if(ticker % 100 == 0)
            //downAttack();

        // only work if the player is enabled
        if (!disabled) {
            if (executing) {
                if (sequence != null) {
                    sequence.next();
                }
            }

            updateHurtBoxes();

            readController();
        }
    }

    //BRIAN ADDED THING, added fake controller input for AI to use
    public void AIreadController(Queue<ButtonEvent> buttonEvents, int dir){
        if (buttonEvents != null) {
            if (buttonEvents.peek() != null) {
                switch (buttonEvents.poll().getButton()) {
                    case ATTACK_BUTTON:
                        attack();
                        break;
                    case SPECIAL_BUTTON:
                        break;
                    case JUMP_BUTTON:
                        jump();
                        break;
                    case SHIELD_BUTTON:
                        break;
                    case TAUNT_BUTTON:
                        break;
                    case START_BUTTON:
                        break;
                    default:
                        break;
                }
            }
            AIupdateMovement(dir);
        }
    }

    private void AIupdateMovement(int dir) {

        if (dir == 1) {
            if (getVelocity().getX() > 0) {
                setVelocity(new DoublePair(-getVelocity().getX(), getVelocity().getY()));
            }

            moveSideways(1);
        } else if (dir == 3) {
            if (getVelocity().getX() < 0) {
                setVelocity(new DoublePair(-getVelocity().getX(), getVelocity().getY()));
            }

            moveSideways(-1);
        } else {
            if (sequence != null && !sequence.getSequence().equals(jump)) {
                executing = false;
            }

            //velocity.addInPlace(new DoublePair(-velocity.getX() / slidiness, 0));
        }

        if (jumps != 2 && velocity.getY() == 0) {
            jumps = 2;
        }
    }

    private void readController() {
        Queue<ButtonEvent> buttonEvents = input.consumeEventsForPlayer(this);
        if (buttonEvents != null) {
            if (buttonEvents.peek() != null) {
                switch (buttonEvents.poll().getButton()) {
                    case ATTACK_BUTTON:
                        attack();
                        break;
                    case SPECIAL_BUTTON:
                        specialAttack();
                        break;
                    case JUMP_BUTTON:
                        jump();
                        break;
                    case SHIELD_BUTTON:
                        break;
                    case TAUNT_BUTTON:
                        break;
                    case START_BUTTON:
                        break;
                    case CONTROLLER_SELECT_BUTTON:
                        break;
                    default:
                        break;
                }
            }
            updateMovement();
        }
    }

    protected void updateHurtBoxes() {
        // determine if we have an attack or a projectile
        if(theAttack != null) {
            //it is a melee attack
            if(theAttack.next()){
                theAttack = null;
                clearDamaged();
            }
        }
        else if(projectile != null) {
            //it is a projectile
            projectile.tick();
            if(projectile == null){
                clearDamaged();
            }
        }
    }

    @Override
    public BufferedImage getSprite() {
        if (!executing || sequence == null) {
            return sprite.getSprite(6, 1, facingLeft);
        } else {
            return sprite.getSprite(sequence.getY(), sequence.getX(), facingLeft);
        }
    }

    @Override
    public double getAspectRatio() {
        return getSprite().getWidth() / (double) getSprite().getHeight();
    }

    private int getPrimaryDirection(DoublePair input) {
        int temp = -1;

        if (input == null) {
            return -1;
        }

        // down = 0, left = 1, up = 2, right = 3 neutral = 4 error = -1
        if (Math.abs(input.getX()) - Math.abs(input.getY()) > Definitions.DEADZONE) {
            if (input.getX() < -Definitions.DEADZONE) {
                temp = 1;
            }
            else if (input.getX() > Definitions.DEADZONE) {
                temp = 3;
            }
        } else if (Math.abs(input.getY()) - Math.abs(input.getX()) > Definitions.DEADZONE) {
            if (input.getY() < -Definitions.DEADZONE) {
                temp = 2;
            }
            else if (input.getY() > Definitions.DEADZONE) {
                temp = 0;
            }
        } else {
            temp = 4;
        }
        return temp;
    }

    private void updateMovement() {
        if(theAttack != null){
            velocity.addInPlace(new DoublePair(-velocity.getX()/slidiness, 0));
            return;
        }
        int dir = getPrimaryDirection(input.getStickForPlayer(this));
        if (dir == 1) {
            if (getVelocity().getX() > 0) {
                setVelocity(new DoublePair(-getVelocity().getX(), getVelocity().getY()));
            }
            moveSideways(-1);
        } else if (dir == 3) {
            if (getVelocity().getX() < 0) {
                setVelocity(new DoublePair(-getVelocity().getX(), getVelocity().getY()));
            }
            moveSideways(1);
        } else {
            if (sequence != null && !sequence.getSequence().equals(jump) && !attacking()) {
                //executing = false;
            }

            velocity.addInPlace(new DoublePair(-velocity.getX() / slidiness, 0));
        }

        if (this.getJumps() != 2 && velocity.getY() == 0 && position.getY() > 1) {
            this.setJumps(2);
        }
    }


    private void moveSideways(int i) {
        currentMaxVelocity = Definitions.MAX_VELOCITY * (Math.pow(Math.abs(input.getStickForPlayer(this).getX()), 2));

        int dir = getPrimaryDirection(input.getStickForPlayer(this));
        if (dir == 1) {
            dir = -1;
        }
        else if (dir == 3) {
            dir = 1;

        }
        if (getVelocity().getX() * dir <= currentMaxVelocity) {
            if (getVelocity().getX() * dir + speed  > currentMaxVelocity) {
                velocity.addInPlace(new DoublePair(dir * currentMaxVelocity - getVelocity().getX(), 0));
            } else {
                velocity.addInPlace(new DoublePair(dir * speed, 0));
            }
            if (dir == -1 && (sequence == null || !sequence.getSequence().equals(moveLeft))) {
                facingLeft = true;
                sequence = new SpriteState(moveLeft, true, 0, this);
            }
            else if (dir == 1 && (sequence == null || !sequence.getSequence().equals(moveRight))) {
                facingLeft = false;
                sequence = new SpriteState(moveRight, true, 0, this);
            }
            executing = true;
        }
        else{
            velocity.addInPlace(new DoublePair(-1 * dir * speed, 0));
        }
    }

    private void jump() {
        if (this.getJumps() > 0) {
            position.setY(position.getY() - 3);
            velocity.addInPlace(0, -5);
            this.setJumps(this.getJumps() - 1);
            executing = true;
            sequence = new SpriteState(jump, false, 0, this);
        }
    }

    private void moveDown() {

    }

    @Override
    public Set<Shape> getCollisions() {
        return colliders;
    }

    private void attack() {
        if (theAttack == null) {
            switch (getPrimaryDirection(input.getStickForPlayer(this))) {
                case 0: // down
                    downAttack();
                    break;
                case 1: // left
                    break;
                case 2: // up
                    upAttack();
                    break;
                case 3: // right
                    break;
                case 4: // neutral
                    neutralAttack();
                    break;
            }
        } else {
            logger.warn("Cannot attack right now, attack is already in progress");
        }
    }

    private void upAttack(){
        int multiplier = facingLeft ? -1 : 1;
        DoublePair[][] offsets = new DoublePair[3][3];
        DoublePair[][] directions = new DoublePair[3][3];
        int[][] damages = new int[3][3];
        int[][] areas = new int[3][3];
        int[] ticks = new int[]{5, 5, 5};

        offsets[0][0] = new DoublePair(multiplier * 30, -20);
        offsets[0][1] = null;
        offsets[0][2] = null;
        offsets[1][0] = offsets[0][0];
        offsets[1][1] = new DoublePair(multiplier * 40, -30);
        offsets[1][2] = new DoublePair(multiplier * 50, -40);
        offsets[2][0] = offsets[0][0];
        offsets[2][1] = null;
        offsets[2][2] = null;

        for(int i = 0; i < areas.length; i++){
            for(int j = 0; j < areas[i].length; j++){
                areas[i][j] = 8;
            }
        }

        for(int i = 0; i < directions.length; i++){
            for(int j = 0; j < directions[i].length; j++){
                directions[i][j] = new DoublePair(multiplier, -5);
            }
        }

        for(int i = 0; i < damages.length; i++){
            for(int j = 0; j < damages[i].length; j++){
                damages[i][j] = 5;
            }
        }

        theAttack = new Attack(offsets, ticks, damages, directions, areas, this);
        sequence = new SpriteState(upAttack, false, 0, this);
        executing = true;
    }

    private void neutralAttack(){
        int multiplier = facingLeft ? -1 : 1;
        DoublePair[][] offsets = new DoublePair[3][3];
        DoublePair[][] directions = new DoublePair[3][3];
        int[][] areas = new int[3][3];
        int[][] damages = new int[3][3];
        int[] ticks = new int[]{5, 5, 5};

        for(int i = 0; i < areas.length; i++){
            for(int j = 0; j < areas[i].length; j++){
                areas[i][j] = 8;
            }
        }

        for(int i = 0; i < damages.length; i++){
            for(int j = 0; j < damages[i].length; j++){
                damages[i][j] = 5;
            }
        }
        offsets[0][0] = new DoublePair(multiplier * 10, -7);
        offsets[0][1] = null;
        offsets[0][2] = null;
        offsets[1][0] = offsets[0][0];
        offsets[1][1] = new DoublePair(multiplier * 25, -7);
        offsets[1][2] = new DoublePair(multiplier * 40, -7);
        offsets[2][0] = offsets[0][0];
        offsets[2][1] = null;
        offsets[2][2] = null;

        for(int i = 0; i < directions.length; i++){
            for(int j = 0; j < directions[i].length; j++){
                directions[i][j] = new DoublePair(multiplier * 2, -1);
            }
        }
        theAttack = new Attack(offsets, ticks, damages, directions, areas, this);
        sequence = new SpriteState(neutralAttack, false, 0, this);
        executing = true;
    }
    public void downAttack(){
        int multiplier = facingLeft ? -1 : 1;
        DoublePair[][] offsets = new DoublePair[6][2];
        DoublePair[][] directions = new DoublePair[6][2];
        int[][] areas = new int[6][2];
        int[][] damages = new int[6][2];
        int[] ticks = new int[]{5, 5, 5, 5, 5, 5};

        offsets[0][0] = null;
        offsets[0][1] = new DoublePair(0, -30);
        offsets[1][0] = offsets[0][1];
        offsets[1][1] = new DoublePair(multiplier * -15, -20);
        offsets[2][0] = offsets[1][1];
        offsets[2][1] = new DoublePair(multiplier * -25, 0);
        offsets[3][0] = offsets[2][1];
        offsets[3][1] = new DoublePair(0, 30);
        offsets[4][0] = offsets[3][1];
        offsets[4][1] = new DoublePair(multiplier * 25, 0);
        offsets[5][0] = offsets[4][1];
        offsets[5][1] = new DoublePair(multiplier * 15, -20);

        for(int i = 0; i < areas.length; i++){
            for(int j = 0; j < areas[i].length; j++){
                areas[i][j] = 10;
            }
        }
        areas[3][1] = 30;
        areas[4][0] = 30;

        for(int i = 0; i < damages.length; i++){
            for(int j = 0; j < damages[i].length; j++){
                damages[i][j] = 10;
            }
        }

        for(int i = 0; i < directions.length; i++){
            for(int j = 0; j < directions[i].length; j++){
                directions[i][j] = new DoublePair(0, 10);
            }
        }
        theAttack = new Attack(offsets, ticks, damages, directions, areas, this);
        sequence = new SpriteState(downAttack, false, 0, this);
        executing = true;
    }
    public Set<Shape> getPain() {
        if(theAttack == null){
            return new HashSet<>();
        }
        return theAttack.getPain();
    }

    @Override
    public int getHeight() {
        return Definitions.DEFAULT_PLAYER_HEIGHT;
    }

    @Override
    public String getName() {
        if (prof == Professor.KUHL) {
            return "DR. KUHL";
        } else if (prof == Professor.LEO) {
            return "LEO";
        } else {
            return "UNKNOWN PROFESSOR";
        }
    }

    /*
    example projectile attack, can be altered down the road
     */
    private void specialAttack(){
        if(projectile == null) {
            int multiplier = facingLeft ? -1 : 1;
            DoublePair pos = new DoublePair(getPosition().getX(), getPosition().getY() - 5.0);
            DoublePair vel = new DoublePair(multiplier * 2.0, 0.0);

            projectile = new KuhlBird(pos, vel, this, Projectiles.KuhlBird, 100);
            PhysicsManager.getInstance().addObject(projectile);
        } else {
            logger.warn("Can only have one of these projectiles at a time.");
        }
    }
}
